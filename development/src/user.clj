(ns user
  (:require
   [integrant.core :as ig]
   [integrant.repl :as ig-repl :refer [go reset halt]]))

(def config {:corespin.rest-api.server/jetty
             {:port 3003
              :join? false}})

(ig-repl/set-prep!
 (fn []
   (ig/load-namespaces config)
   (ig/prep config)))
