(ns corespin.sqlite-store.interface
  (:require
   [corespin.sqlite-store.core :as core]))

(defn injest-investigation-data
  "Injest investigation data into investigation db"
  [data]
  (core/injest-investigation-data data))
