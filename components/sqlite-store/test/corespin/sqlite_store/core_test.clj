(ns corespin.sqlite-store.core-test
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test :as test :refer :all]
   [clojure.test.check.generators :as gen]
   [corespin.specs]
   [corespin.specs.json-reader :as s.json-reader]
   [corespin.sqlite-store.core :as sqlite-store]
   [honey.sql :as sql]
   [next.jdbc.result-set :as rs]
   [next.jdbc.sql :refer [query]]))

(def db (atom nil))

(use-fixtures :once
  (fn [f]
    (let [file (doto (java.io.File/createTempFile "test" ".sqlite3")
                 (.deleteOnExit))]
      (reset! db (sqlite-store/init-ds (.getAbsolutePath file)))
      (f))))

(defn query-db [qm]
  (query @db (sql/format qm) {:builder-fn rs/as-unqualified-maps}))

(deftest injest-investigation-data-test
  (testing "Absorbed investigation data in DB should match the source"
    (let [inv-data (gen/generate (s/gen ::s.json-reader/inv-data))
          q {:select [:*] :from :feed}]
      (sqlite-store/injest-investigation-data inv-data @db)
      (is (= (:feeds inv-data) (query-db q)) "feeds are matching")

      (is (= (->> inv-data :tags (mapcat val) set)
             (->> (query-db (assoc q :from :tag))
                  (map :tag) set))
          "tags are matching")

      (is (= (->> inv-data :indicators (mapcat val)
                  (sort-by :id))
             (->> (query-db (assoc q :from :indicator))
                  (sort-by :id)))
          "indicators are matching"))))
