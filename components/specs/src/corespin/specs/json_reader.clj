(ns corespin.specs.json-reader
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test.check.generators :as gen]
   [spec-tools.impl :as st]))

(s/def ::industries string?)
(s/def ::targeted_countries string?)

(s/def ::feed
  (s/keys :req-un [:feed/id
                   :feed/name
                   :feed/description
                   :feed/created
                   :feed/modified
                   :feed/author_name
                   :feed/revision
                   :feed/tlp
                   :feed/adversary
                   ::industries
                   ::targeted_countries]))

(s/def ::indicator
  (s/keys :req-un [:indicator/id
                   :indicator/description
                   :indicator/created
                   :indicator/indicator
                   :indicator/type]))

(s/def ::feeds (s/coll-of ::feed :kind vector?))
(s/def ::tags (s/map-of :feed/id :feed/tag))
(s/def ::indicators (s/map-of :feed/id (s/coll-of ::indicator)))

(s/def ::inv-data
  (s/with-gen
    (s/map-of keyword? any?)
    (fn []
      (gen/fmap
       (fn [feeds]
         (->>
          feeds
          (reduce
           (fn [acc n]
             (let [fks (->> (s/form ::feed) st/extract-keys (map st/un-key))
                   feed (-> n
                            (select-keys fks)
                            (assoc :industries (gen/generate (s/gen ::industries))
                                   :targeted_countries (gen/generate (s/gen ::targeted_countries))))
                   iks (->> (s/form ::indicator) st/extract-keys (map st/un-key))
                   indicators (->> n :indicators (map #(select-keys % iks)))]
               (-> acc
                   (update :feeds conj feed)
                   (assoc-in [:tags (:id n)] (:tags n))
                   (assoc-in [:indicators (:id n)] indicators))))
           {:feeds [] :tags {} :indicators {}})))
       (s/gen :feed/feeds)))))

