(ns corespin.sqlite-store.interface
  (:require
   [corespin.sqlite-store.core :as core]))

;; (def ds "jdbc:sqlite:./indicators.sqlite3")

;; (require '[next.jdbc :as jdbc])

;; (jdbc/execute! ds ["
;;   create table address (
;;     name  TEXT NOT NULL,
;;     email TEXT NOT NULL UNIQUE
;;   )
;; "])


(defn injest-investigation-data
  "Injest JSON data into investigation db"
  [file]
  (core/injest-investigation-data file))
