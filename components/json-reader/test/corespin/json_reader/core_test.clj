(ns corespin.json-reader.core-test
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test :refer :all]
   [clojure.test.check.generators :as gen]
   [corespin.json-reader.core :as core]))

(deftest kv-match?-test
  (testing "simple maps"
    (are [m k search-str exp] (= exp (core/kv-match? m k search-str))
      {}               nil  nil   false
      {}               :foo ""    false
      {}               :foo "foo" false
      {:foo nil}       :foo nil   false
      {:foo "bar"}     :foo nil   false
      {:foo "bar"}     :foo "bar" true
      {:foo
       "there-is-bar"} :foo "bar" true
      {:foo
       "there-is-bar"} :bar "bar" false
      {:foo
       "is-bar-there"} :foo "bar" true
      {:foo 1}         :foo "1"   true

      {:foo
       #inst "1970-01-02T18:14:00.249-00:00"} :foo "01-02" true
      {:foo
       #inst "1970-01-02T18:14:00.249-00:00"} :foo "1970"  true
      {:foo
       #inst "1970-01-02T18:14:00.249-00:00"} :foo "1991"  false))

  (testing "simple nested maps"
    (are [m k search-str exp] (= exp (core/kv-match? m k search-str))
      {:foo {:bar {:zap 1}}}  :zap "1" true
      {:foo {:bar {:zap 1}}}  :zap ""  false
      {:foo {:bar {:zap 1}}}  :zap nil false
      {:foo {:bar {:zap 42}}} :zap "4" true
      ;; search is case sensitive
      {:foo {:bar {:zap "Vivamus id ENIM."}}} :zap "enim" false
      {:foo {:bar {:zap "Vivamus id ENIM."}}} :zap "ENIM" true)))

(comment
  ;; TODO: consider adding some tests with generated nested maps
  (def nested-map-gen
    "Nested map generator."
    (gen/recursive-gen
     (fn [inner]
       (gen/one-of [(gen/map gen/keyword inner)]))
     (gen/one-of [gen/string-alphanumeric
                  gen/boolean
                  gen/small-integer
                  (s/gen inst?)]))))
