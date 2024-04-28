(ns corespin.rest-api.routes
  (:require
   [corespin.json-reader.interface :as json-reader]
   [corespin.sqlite-store.interface :as sqlite-store]
   [reitit.ring.middleware.multipart :as multipart]
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]))

(def routes
  [["/swagger.json"
    {:get {:no-doc true
           :swagger {:info {:title "corespin API"}
                     :basePath "/"} ;; prefix for all paths
           :handler (swagger/create-swagger-handler)}}]

   ["/upload-investigation-data"
    {:post
     {:summary "upload JSON data with investigation feed"
      :parameters {:multipart {:file multipart/temp-file-part}}
      :handler (fn [{{{:keys [file]} :multipart} :parameters}]
                 (let [inv-data (json-reader/parse-investigation-data (:tempfile file))
                       {:keys [feeds indicators]} (sqlite-store/injest-investigation-data inv-data)]
                   {:status 200
                    :body {:result (format "imported %s feeds with %s indicators" feeds indicators)}}))}}]
   ["/indicators"
    {:get {:summary "get list of all indicators"
           :handler (fn [_]
                      {:status 200
                       :body "TODOS INDICATORES"})}}]])
