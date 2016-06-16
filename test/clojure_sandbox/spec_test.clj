(ns clojure-sandbox.spec-test
  (:use clojure.test
        clojure-sandbox.spec)
  (:require [clojure.spec :as s]))

(deftest predicates

  (testing "simple predicate"
    (is (= 1000 (s/conform even? 1000)))
    (is (= :clojure.spec/invalid (s/conform even? 1001)) "Non-conformance results in :clojure.spec/invalid")

    )


  )
