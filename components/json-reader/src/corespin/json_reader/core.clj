(ns corespin.json-reader.core
  (:require
   [cheshire.core :as json]
   [clojure.java.io :as io]))

(defn serialize
  "Serializes values of a map turning them into json strings"
  [m]
  (zipmap
   (keys m)
   (->> m vals (map #(when (seq %)
                       (json/generate-string %))))))

(defn deserialize
  "Parses a map with json stringified values."
  [m]
  (zipmap
   (keys m)
   (->> m vals (map json/parse-string))))

(defn parse-investigation-data [json-file]
  (with-open [reader (io/reader json-file)]
    (->>
     (json/parse-stream reader true)
     (reduce
      (fn [acc n]
        (let [feed (-> n (select-keys [:id
                                       :name
                                       :description
                                       :created
                                       :modified
                                       :author_name
                                       :revision
                                       :tlp
                                       :adversary])
                       (merge (-> n
                                  (select-keys [:industries
                                                :targeted_countries])
                                  serialize)))
              indicators (->> n :indicators
                              (map #(select-keys % [:id
                                                    :description
                                                    :created
                                                    :indicator
                                                    :type])))]
          (-> acc
              (update :feeds conj feed)
              (assoc-in [:tags (:id n)] (:tags n))
              (assoc-in [:indicators (:id n)] indicators))))
      {:feeds [] :tags {} :indicators {}}))))
