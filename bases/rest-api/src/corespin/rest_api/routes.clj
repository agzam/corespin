(ns corespin.rest-api.routes
  (:require
   [reitit.ring.middleware.multipart :as multipart]
   [reitit.swagger :as swagger]
   [corespin.sqlite-store.interface :as sqlite-store]
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
                 (let [res (sqlite-store/injest-investigation-data
                            file)]
                   (println res))
                 {:status 200
                  :body "Uploaded yo!"})
      :swagger {:consumes ["multipart/form-data"]}
      }}
    ]
   ["/indicators"
    {:get {:summary "get list of all indicators"
           :handler (fn [_]
                      {:status 200
                       :body "TODOS INDICATORES"})}}]])
