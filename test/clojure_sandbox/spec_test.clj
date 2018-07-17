(ns clojure-sandbox.spec-test
  (:use clojure.test)
  (:require [clojure.spec.alpha :as s]
            [clojure-sandbox.spec :refer :all])
  (:import (java.util Date)))

(deftest basic-predicates "http://clojure.org/guides/spec#_predicates"

  (testing "using `conform` to conform the value"
    (is (= 1000 (s/conform even? 1000)))
    (is (= :clojure.spec.alpha/invalid (s/conform even? 1001)) "Non-conformance results in :clojure.spec/invalid"))

  (testing "using `valid?` to just test validity"
    (is (s/valid? even? 10))
    (is (not (s/valid? even? 11))))

  (testing "using function literals and sets as predicates"
    (is (s/valid? #(> % 10) 11))
    (let [suits #{:hearts :clubs :diamonds :spades}]
      (is (s/valid? suits :hearts))
      (is (not (s/valid? suits :frogs))))))


(deftest registry "http://clojure.org/guides/spec#_registry"

  (testing "using basic registry entries as specs"
    (is (s/valid? :clojure-sandbox.spec/date (Date.)))
    (is (= (s/conform :clojure-sandbox.spec/suit :club) :club))))


(deftest composing-predicates "http://clojure.org/guides/spec#_composing_predicates"

  (testing "composing basic predicates with `and`"
    (is (not (s/valid? :clojure-sandbox.spec/big-even :foo)))
    (is (not (s/valid? :clojure-sandbox.spec/big-even 10)))
    (is (not (s/valid? :clojure-sandbox.spec/big-even 1001)))
    (is (s/valid? :clojure-sandbox.spec/big-even 1002)))

  (testing "composing basic predicates with `or`"
    (s/def :clojure-sandbox.spec/name-or-id (s/or :name string?
                                                  :id   int?))
    (is (s/valid? :clojure-sandbox.spec/name-or-id "abc"))
    (is (s/valid? :clojure-sandbox.spec/name-or-id 100))
    (is (not (s/valid? :clojure-sandbox.spec/name-or-id :foo)))

    (is (= (s/conform :clojure-sandbox.spec/name-or-id "abc") [:name "abc"])
        "Conforming a spec created with `or` returns a vector describing conformation")))


(deftest explain "http://clojure.org/guides/spec#_explain"

   (testing "using `explain` to report conformation to *out*"
     (is (= (.trim (with-out-str (s/explain :clojure-sandbox.spec/suit :club))) "Success!"))
     (is (-> (s/explain :clojure-sandbox.spec/suit 42)
              with-out-str
              (.contains "val: 42 fails spec"))))

   (testing "using `explain-str` to avoid the need for with-out-str"
     (is (= (.trim (s/explain-str :clojure-sandbox.spec/suit :club)) "Success!")))

   (testing "using `explain-data` to get a data structure back"
     (let [result (s/explain-data :clojure-sandbox.spec/name-or-id :foo)]
       (is (map? result) "Result of validation is a map")
       (is (contains? result :clojure.spec.alpha/problems) "Result contains `:problems` key")
       (let [problems (:clojure.spec.alpha/problems result)]
         (is (= (count problems) 2) "Result contains two problems")
         (is (= (-> problems
                    first
                    :path
                    first) :name) "First problem is in the `:name` path")
         (is (= (-> problems
                    second
                    :path
                    first) :id) "Second problem is in the `:id` path")))))


(deftest entity-maps "http://clojure.org/guides/spec#_entity_maps"

  (testing "basic specs using s/keys with qualified keys"

    (is (s/valid? :clojure-sandbox.spec/person {:clojure-sandbox.spec/first-name "Martin"
                                                :clojure-sandbox.spec/last-name "Rist"
                                                :clojure-sandbox.spec/email "me@example.com"})
        "valid")
    (is (s/valid? :clojure-sandbox.spec/person {:clojure-sandbox.spec/first-name "Martin"
                                                :clojure-sandbox.spec/last-name "Rist"
                                                :clojure-sandbox.spec/email "me@example.com"
                                                :clojure-sandbox.spec/phone "12345678"})
        "valid with optional key")
    (is (s/valid? :clojure-sandbox.spec/person {:clojure-sandbox.spec/first-name "Martin"
                                                :clojure-sandbox.spec/last-name "Rist"
                                                :clojure-sandbox.spec/email "me@example.com"
                                                :clojure-sandbox.spec/foo ::bar}) "valid with extra keys")

    (is (not (s/valid? :clojure-sandbox.spec/person {::first-name "Martin" ::last-name "Rist"})) "missing required key")
    (is (not (s/valid? :clojure-sandbox.spec/person {::first-name "Martin" ::last-name "Rist" ::email "not-an-email"})) "invalid email")
    (is (not (s/valid? :clojure-sandbox.spec/person {::first-name "Martin" ::last-name "Rist" ::email "me@example.com" ::phone :not-a-phone})) "invalid optional key value"))

  (testing "using unqualified keys"
    (is (not (s/valid? :clojure-sandbox.spec/person {:first-name "Martin" :last-name "Rist" :email "me@example.com"})) "invalid against qualified spec")
    (is (s/valid? :unq/person {:first-name "Martin" :last-name "Rist" :email "me@example.com"}) "valid against unqualified spec"))

  (testing "using records"
    (is (s/valid? :unq/person (->Person "Martin" "Rist" "me@example.com" nil)))
    (is (not (s/valid? :unq/person (->Person "Martin" "Rist" "me@example.com" :not-a-phone))))))



(deftest multi-specs "http://clojure.org/guides/spec#_multi_spec"

  (testing "search event messages"
    (is (s/valid? :event/event {:event/type       :event/search
                                :event/timestamp  12345
                                :search/url       "http://www.example.com"}))

    (is (not (s/valid? :event/event {:event/type      :event/search
                                     :event/timestamp 12345}))))

  (testing "error messages"
    (is (s/valid? :event/event {:event/type       :event/error
                                :event/timestamp  12345
                                :error/code       666
                                :error/message    "Miscellaneous error"}))
    (is (not (s/valid? :event/event {:event/type      :event/error
                                     :event/timestamp 12345
                                     :error/code      :not-an-error-code})))))


(deftest collections "http://clojure.org/guides/spec#_collections"

  (testing "homogeneous collections of arbitrary size"
    (is (s/valid? (s/coll-of keyword?) [:a :b :c]))
    (is (s/valid? (s/coll-of keyword? :kind vector?) [:a :b :c]))
    (is (s/valid? (s/coll-of keyword? :kind list?) (list :a :b :c)))
    (is (s/valid? (s/coll-of number? :count 3) [1 2 3]))
    (is (not (s/valid? (s/coll-of number? :count 3) [1 2])))
    (is (not (s/valid? (s/coll-of number? :distinct true) [1 2 3 4 4]))))

  (testing "tuples"
    (is (s/valid? (s/tuple double? int?) [1.5, 2]))
    (is (not (s/valid? (s/tuple double? int?) [1.5, 2.5]))))

  (testing "maps"
    (is (s/valid? (s/map-of keyword? int?) {:foo 1 :bar 2}))
    (is (not (s/valid? (s/map-of keyword? int?) {:foo :blort :bar 2})))))



(deftest card-game "http://clojure.org/guides/spec#_a_game_of_cards"

  (testing "valid players"

    (is (s/valid? :clojure-sandbox.spec/player
                  #:clojure-sandbox.spec{:name  "Kenny Rogers"
                                         :score 100
                                         :hand  [[10 :club] [:king :heart]]}))))