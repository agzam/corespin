(ns corespin.rest-api.routes
  (:require
   [clojure.spec.alpha :as s]
   [corespin.json-reader.interface :as json-reader]
   [corespin.sqlite-store.interface :as sqlite-store]
   [reitit.ring.middleware.multipart :as multipart]
   [reitit.swagger :as swagger]))

(s/def :indicator/type string?)
(s/def :indicator/tlp #{"red" "amber" "green" "white"})
(s/def :indicator/indicator string?)
(s/def :page/limit nat-int?)
(s/def :page/offset nat-int?)

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
      :handler
      (fn [{{{:keys [file]} :multipart} :parameters}]
        (let [inv-data (json-reader/parse-investigation-data (:tempfile file))

              {:keys [feeds indicators]}
              (sqlite-store/injest-investigation-data inv-data)]
          {:status 200
           :body {:result
                  (format "imported %s feeds with %s indicators"
                          feeds indicators)}}))}}]
   ["/indicators"
    {:get {:summary "get list of all indicators"
           :parameters {:query (s/keys :opt-un [:indicator/type
                                                :indicator/tlp
                                                :indicator/indicator
                                                :page/limit
                                                :page/offset])}
           :handler (fn [{{query-params :query} :parameters}]
                      (let [res (sqlite-store/get-indicators query-params)]
                        {:status 200
                         :body {:data res}}))}}]])
