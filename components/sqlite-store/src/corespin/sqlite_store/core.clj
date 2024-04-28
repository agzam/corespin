(ns corespin.sqlite-store.core
  (:require
   [next.jdbc :as jdbc :refer [execute!]]))

(def dbfile "./investigation.sqlite3")
(def ds "sqlite datasource" (atom nil))

(defn ds-ok []
  (execute! @ds ["SELECT 1"]))

(def create-tables
  ["create table if not exists feed (
    id text primary key,
    name text,
    description text,
    tlp text,  /* serialized */
    created text,
    modified text,
    author_name text,
    revision integer,
    adversary text,
    industries text, /* serialized */
    'references' text, /* serialized */
    targeted_countries text /* serialized */
)"

   "create table if not exists tag (
    id integer primary key,
    tag text not null)"

   "create table if not exists feed__tags (
    feed_id text,
    tag_id integer)"

   "create table if not exists indicator (
    id integer primary key,
    description text,
    created text,
    title text,
    indicator text)"

   "create table if not exists feed__indicators (
    feed_id text,
    indicator_id integer)"

   "create table if not exists indicator_type (
    id integer primary key,
    type text)"

   "create table if not exists indicator__indicator_types (
    indicator_id integer,
    type_id integer)"])

(defn init-ds
  "Initialize datasource"
  []
  (when-not (.exists (java.io.File. dbfile))
    (reset! ds (format "jdbc:sqlite:%s" dbfile))
    (doseq [table create-tables]
      (execute! @ds [table]))))


(defn injest-investigation-data [file]
  ;; check if db exists if not, then create it
  (println "injesting"))
