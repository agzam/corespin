(ns corespin.data-specs.date
  (:require
   [clojure.test.check.generators :as gen]
   [clojure.spec.alpha :as s]))

(def datetime-str-gen
  (gen/fmap
   (fn [date-instance]
     (let [fmt (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
           zdt (java.time.ZonedDateTime/ofInstant (.toInstant date-instance) (java.time.ZoneId/of "Z"))]
       (.format fmt zdt)))
   (s/gen inst?)))

;; RFC 3339/ISO 8601 datetime string
(s/def ::datetime-str
  (s/with-gen
    (s/and string? #(re-matches #"\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d{3}Z" %))
    (fn [] datetime-str-gen)))
