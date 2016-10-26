(ns clojure-sandbox.spec-test
  (:use clojure.test
        clojure-sandbox.spec)
  (:require [clojure.spec :as s])
  (:import (java.util Date)))

(deftest basic-predicates "http://clojure.org/guides/spec#_predicates"

  (testing "using `conform` to conform the value"
    (is (= 1000 (s/conform even? 1000)))
    (is (= :clojure.spec/invalid (s/conform even? 1001)) "Non-conformance results in :clojure.spec/invalid"))

  (testing "using `valid?` to just test validity"
    (is (s/valid? even? 10))
    (is (not (s/valid? even? 11))))

  (testing "using function literals and sets as predicates"
    (is (s/valid? #(> % 10) 11))
    (let [suits #{:hearts :clubs :diamonds :spades}]
      (is (s/valid? suits :hearts))
      (is (not (s/valid? suits :frogs))))))


(deftest registry "http://clojure.org/guides/spec#_registry"

  ; Use `s/def` to define a registry of some basic specs
  (s/def ::date inst?)
  (s/def ::suit #{:heart :club :diamond :spade})

  (testing "using basic registry entries as specs"
    (is (s/valid? ::date (Date.)))
    (is (= (s/conform ::suit :club) :club))))


(deftest composing-predicates "http://clojure.org/guides/spec#_composing_predicates"

  (testing "composing basic predicates with `and`"
    (s/def ::big-even (s/and int? even? #(> % 1000)))
    (is (not (s/valid? ::big-even :foo)))
    (is (not (s/valid? ::big-even 10)))
    (is (not (s/valid? ::big-even 1001)))
    (is (s/valid? ::big-even 1002)))

  (testing "composing basic predicates with `or`"
    (s/def ::name-or-id (s/or :name string?
                              :id   int?))
    (is (s/valid? ::name-or-id "abc"))
    (is (s/valid? ::name-or-id 100))
    (is (not (s/valid? ::name-or-id :foo)))

    (is (= (s/conform ::name-or-id "abc") [:name "abc"])
        "Conforming a spec created with `or` returns a vector describing conformation"))


  )