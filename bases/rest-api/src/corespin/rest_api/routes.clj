(ns corespin.rest-api.routes
  (:require
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]))

(def routes
  [["/swagger.json"
    {:get {:no-doc true
           :swagger {:info {:title "corespin API"}
                     :basePath "/"} ;; prefix for all paths
           :handler (swagger/create-swagger-handler)}}]
   ["/indicators"
    {:get {:summary "get list of all indicators"
           :handler (fn [_]
                      {:status 200
                       :body "TODOS INDICATORES"})}}]])
