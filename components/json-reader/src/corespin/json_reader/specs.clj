(ns corespin.json-reader.specs
  (:require
   [clojure.spec.alpha :as s]
   [corespin.data-specs.specs]))

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

(s/def ::feeds (s/coll-of ::feed))
(s/def ::tags (s/map-of :feed/id :feed/tag))
(s/def ::indicators (s/map-of :feed/id (s/coll-of ::indicator)))

(s/def ::inv-data
  (s/keys :req-un [::feeds ::tags ::indicators]))
