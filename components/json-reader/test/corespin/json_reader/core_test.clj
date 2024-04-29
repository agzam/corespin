(ns corespin.json-reader.core-test
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test :refer :all]
   [clojure.test.check.generators :as gen]
   [corespin.data-specs.specs]
   [corespin.json-reader.core :as core]))

(deftest feeds->inv-data-test
  (testing "correctness of data transform"
    ;; it's not an isomorphic transform -
    ;; the function discards some keys. That means
    ;; we can't simply test the function by
    ;; reversing the operation and comparing with
    ;; the original feeds data
    (let [feeds (gen/generate (s/gen (s/coll-of :feed/feed)))
          inv-data (core/feeds->inv-data feeds)
          feed-ks [:id
                   :name
                   :description
                   :created
                   :modified
                   :author_name
                   :revision
                   :tlp
                   :adversary]
          indicator-ks [:id
                        :description
                        :created
                        :indicator
                        :type]]
      (is (= (count feeds)
             (-> inv-data :feeds count)))
      (is (= (->> feeds (map #(select-keys % feed-ks)))
             (->> inv-data :feeds (map #(select-keys % feed-ks)))))
      (is (= (->> feeds (mapcat :indicators) count)
             (->> inv-data :indicators (mapcat val) count)))
      (is (= (->> feeds (mapcat :indicators)
                  (map #(select-keys % indicator-ks))
                  (sort-by :id))
             (->> inv-data :indicators (mapcat val)
                  (map #(select-keys % indicator-ks))
                  (sort-by :id))))
      (is (= (->> feeds (mapcat :tags))
             (->> inv-data :tags (mapcat val)))))))
