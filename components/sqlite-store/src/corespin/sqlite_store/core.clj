(ns corespin.sqlite-store.core
  (:require
   [clojure.string :as str]
   [corespin.json-reader.interface :as json-reader]
   [honey.sql :as sql]
   [honey.sql.helpers :as h :refer [insert-into values]]
   [next.jdbc :as jdbc :refer [execute!]]
   [next.jdbc.result-set :as rs]
   [next.jdbc.sql :refer [insert! query]]))

(def dbfile "./investigation.sqlite3")
(def ds "sqlite datasource" (atom nil))

(sql/register-clause!
 :insert-or-ignore-into
 (fn [clause x]
   (let [[sql & params]
         (if (ident? x)
           (sql/format-expr x)
           (sql/format-dsl x))]
     (into [(str (sql/sql-kw clause) " " sql)] params)))
 :insert-into)

(def create-tables
  ["create table if not exists feed (
    id text primary key,
    name text,
    description text,
    created text,
    modified text,
    author_name text,
    revision integer,
    adversary text,
    tlp text,
    industries text,        /* serialized */
    targeted_countries text /* serialized */
);"

   "create table if not exists tag (
    id integer primary key,
    tag text,
    unique(tag));"

   "create table if not exists feed__tags (
    feed_id text not null,
    tag_id integer not null,
    unique(feed_id, tag_id));"

   "create table if not exists indicator (
    id integer primary key,
    description text,
    created text,
    indicator text,
    'type' text);"

   "create table if not exists feed__indicators (
    feed_id text not null,
    indicator_id integer not null,
    unique(feed_id, indicator_id));"])

(defn init-ds
  "Initialize datasource"
  []
  (let [create-tables? (not (.exists (java.io.File. dbfile)))]
    (reset! ds (format "jdbc:sqlite:%s" dbfile))
    (when create-tables?
      (doseq [table create-tables]
        (execute! @ds [table])))))

(defn injest-investigation-data
  [data]
  (init-ds)
  (let [counters (atom {:feeds 0 :indicators 0})]
    (doseq [{:keys [id] :as feed} (:feeds data)
            :let [tags (-> data :tags (get id))
                  indicators (-> data :indicators (get id))]]
      (jdbc/with-transaction [tx @ds]
        (let [feed-ct (->> {:insert-or-ignore-into :feed :values [feed]}
                           sql/format (execute! tx)
                           first :next.jdbc/update-count)]
          (doseq [tag tags]
            (->> {:insert-or-ignore-into :tag
                  :values [{:tag tag}]}
                 sql/format (execute! tx))
            (let [tag-id (->> {:select [:id]
                               :from :tag
                               :where [:= :tag tag]}
                              sql/format
                              (query tx)
                              first :tag/id)]
              (->> {:insert-or-ignore-into :feed__tags
                    :values [{:feed_id id :tag_id tag-id}]}
                   sql/format (execute! tx))))
          (doseq [indicator indicators]
            (let [ind-ct (->> {:insert-or-ignore-into :indicator :values [indicator]}
                              sql/format (jdbc/execute! tx)
                              first :next.jdbc/update-count)]
              (->> {:insert-or-ignore-into :feed__indicators
                    :values [{:feed_id id
                              :indicator_id (:id indicator)}]}
                   sql/format (jdbc/execute! tx))
              (swap! counters update :indicators + ind-ct)))
          (swap! counters update :feeds + feed-ct))))
    @counters))

(defn add-where-clause
  [q {:keys [type tlp indicator]}]
  (merge
   q
   {:where (cond-> [:and]
             type (conj [:= :i.type type])
             indicator (conj [:= :i.indicator indicator])
             tlp (conj [:= :f.tlp tlp]))}))

(defn get-indicators
  ""
  [{:keys [_type _tlp _indicator
           limit offset] :as params}]
  (init-ds)
  (let [q (cond-> {:select [:f.* :i.*
                            [[:raw (str "(SELECT group_concat(t.tag, ', ') "
                                        "FROM tag AS t "
                                        "JOIN feed__tags AS ft ON ft.tag_id = t.id "
                                        "WHERE ft.feed_id = f.id)")]
                             :tags]]
                   :from [[:indicator :i]]
                   :join [[:feed__indicators :fi] [:= :fi.feed_id :f.id]
                          [:feed :f] [:= :i.id :fi.indicator_id]]
                   :where [:and [:= :i.indicator "123.151.149.222"]]
                   :group-by [:i.id]
                   :limit 100}

            true (add-where-clause params)
            limit (merge {:limit limit})
            offset (merge {:offset offset})
            true (sql/format))]
    (query @ds q {:builder-fn rs/as-unqualified-kebab-maps})))
