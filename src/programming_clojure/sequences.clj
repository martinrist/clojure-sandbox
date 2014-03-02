(ns programming-clojure.sequences
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.xml :as xml]
            [clojure.set :refer :all])
  (:import (java.io StringReader File)))

(def my-list '(1 2 3 4 5))
(def my-vec [1 2 3 4 5])
(def my-set #{50 40 30 20 10})
(def my-sorted-set (into (sorted-set) my-set))
(def my-map {:a 1 :b 2 :c 3 :d 4 :e 5})

(def languages #{"java" "c" "d" "clojure"})
(def beverages #{"java" "chai" "pop"})

(def integers (iterate inc 0))

(defn integers-less-than
  [n]
  (take-while #(< % n) integers))

(def text "The quick brown fox jumps over the lazy dog")
(def words (clojure.string/split text #"\W+"))

;(def xml (xml/parse (File. "src/programming_clojure/compositions.xml")))
(def xml (xml/parse "src/programming_clojure/compositions.xml" ))