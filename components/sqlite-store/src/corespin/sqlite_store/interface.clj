(ns corespin.sqlite-store.interface
  (:require
   [corespin.sqlite-store.core :as core]))

(defn injest-investigation-data
  "Injest investigation data into investigation db"
  [data]
  (core/injest-investigation-data data))

(defn get-indicators
  "Retrieves indicators data from db"
  [{:keys [_type _tlp _indicator
           _limit _offset] :as params}]
  (core/get-indicators params))
