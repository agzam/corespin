(ns corespin.json-reader.interface
  (:require
   [corespin.json-reader.core :as core]))

(defn parse-investigation-data
  "Reads and parses investigation feed JSON.
  returns map of feeds, indicators and tags."
  [json-file]
  (core/parse-investigation-data json-file))
