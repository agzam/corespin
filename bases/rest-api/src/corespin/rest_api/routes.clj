(ns corespin.rest-api.routes)

(def routes
 [#_["/" {:get {:handler (fn [_]
                           {:status 200
                            :body   (index-page)})}}]
     ["/status"
      {:get {:handler (constantly
                       {:status 200
                        :body   {:status :okay}})}}]])
