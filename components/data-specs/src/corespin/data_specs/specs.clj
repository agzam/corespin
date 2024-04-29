(ns corespin.data-specs.specs
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test.check.generators :as gen]
   [corespin.data-specs.date :as date]))

(s/def :mongo/object-id
  (let [hex-char (gen/elements (concat (map str (range 0 10))
                                       (map char (range 97 103))))]
    (s/with-gen
      (s/and string? #(= 24 (count %))
             #(re-matches #"[a-f0-9]{24}" %))
      (fn [] (gen/bind (gen/vector hex-char 24)
                       #(gen/return (apply str %)))))))

(s/def :tlp/colors #{"red" "amber" "green" "white"})

(s/def :feed/adversary string?)
(s/def :feed/author_name string?)
(s/def :feed/created ::date/datetime-str)
(s/def :feed/description string?)
(s/def :feed/extract_source (s/coll-of string?))
(s/def :feed/id :mongo/object-id)
(s/def :feed/industries (s/coll-of string?))
(s/def :feed/modified ::date/datetime-str)
(s/def :feed/more_indicators boolean?)
(s/def :feed/name string?)
(s/def :feed/public nat-int?)
(s/def :feed/references (s/coll-of string?))
(s/def :feed/revision nat-int?)
(s/def :feed/tag string?)
(s/def :feed/tags (s/coll-of :feed/tag :kind vector?
                             :max-count 10))
(s/def :feed/targeted_countries (s/coll-of string?))
(s/def :feed/tlp :tlp/colors)

(s/def :indicator/content string?)
(s/def :indicator/created ::date/datetime-str)
(s/def :indicator/description string?)
(s/def :indicator/id nat-int?)
(s/def :indicator/indicator string?)
(s/def :indicator/title string?)
(s/def :indicator/type string?)

(s/def :feed/indicators
  (s/coll-of (s/keys :req-un [:indicator/content
                              :indicator/created
                              :indicator/description
                              :indicator/id
                              :indicator/indicator
                              :indicator/title
                              :indicator/type])))

(s/def :feed/feed
  (s/keys :req-un [:feed/adversary
                   :feed/author_name
                   :feed/created
                   :feed/description
                   :feed/extract_source
                   :feed/id
                   :feed/indicators
                   :feed/industries
                   :feed/modified
                   :feed/more_indicators
                   :feed/name
                   :feed/public
                   :feed/references
                   :feed/revision
                   :feed/tag
                   :feed/tags
                   :feed/targeted_countries
                   :feed/tlp]))

