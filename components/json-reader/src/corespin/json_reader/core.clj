(ns corespin.json-reader.core
  (:require [cheshire.core :as json]
            [clojure.string :as str]
            [clojure.walk :as walk]))

;; TODO: don't use hardcoded values, maybe use env
(def MAX_CACHE_AGE "in minutes" 60)
(def MAX_FILE_SIZE
  "How big the file size in MBs must be to consider it streaming." 50)

(def content-cache
  "Cached content of JSON files (pre-parsed)"
  (atom {}))

(defn load-file
  "Loads the file and stores its content in `content-cache`"
  [filepath]
  (let [content (json/parse-string (slurp filepath) true)]
    (swap! content-cache :assoc filepath {:content content
                                          :read-at (java.util.Date.)})))

(defn kv-match?
  "Traverses deeply nested map, and determines if there is a key `k` and the string
   representation of its value includes `search-str`."
  [m k search-str]
  (let [state (atom false)]
    (try
      (walk/prewalk
       (fn [node]
         (when (and (map-entry? node)
                    (= (first node) k)
                    (not ((some-fn map? vector? set?) (second node)))
                    (not (str/blank? search-str))
                    (str/includes? (pr-str (second node))
                                   search-str))
           (swap! state (fn [_] true))
           ;; Using exception to break out of the walk early is a hack, as exceptions are not intended for flow control,
           ;; I guess this is acceptable approach given that there's no out-of-the-box way to stop the walk early.
           ;; Perhaps it's better to re-implement this with loop/recur, but I'd first benchmark it to find how great is
           ;; the impact of try/catch
           (throw (RuntimeException. "")))
         node)
       m)
      (catch RuntimeException e))
    @state))

(defn find-maps
  "Searches through a list of deeply nested maps,
   and returns a sequence of all maps where the given `key` is associated with a value
   that contains the specified `search-str`

   Keys are sought on each level of each map, not just at the top level."
  [coll key search-str])

(defn search
  [filepath key search-str]
  ;; if it's in cache we use cache
  ;; if the cache is expired, we re-read the file
  ;; file not in cache - we read the file
  ;; file is too big - we don't read
  )

