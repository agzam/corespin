(ns corespin.json-reader.interface
  (:require [corespin.json-reader.core :as core]))

(defn search
  "Searches through a deeply nested JSON file,
   and returns a sequence of all objects where the given `key` is associated with a value
   that contains the specified `search-str`

   Keys are sought on each level, not just at the top level of the structure."
  [filepath key search-str]
  (core/search filepath key search-str))

(defn indicators-all
  "Returns all indicators in a given file."
  [filepath]
  (core/indicators-all filepath))
