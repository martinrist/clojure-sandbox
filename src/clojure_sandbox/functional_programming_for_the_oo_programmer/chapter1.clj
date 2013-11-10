(ns clojure-sandbox.functional-programming-for-the-oo-programmer.chapter1)

(:doc "Exercises from 'Functional Programming for the OO Programmer', Chapter 1 - 'Just Enough Clojure'")

(defn second
  "Exercise 1 - return the second element in list"
  [list]
  (first (rest list))

(defn third-v1
  "Exercise 2 - return the third element in list (version 1)"
  [list]
  (first (rest (rest list))))

(defn third-v2
  "Exercise 2 - return the third element in list (version 2)"
  [list]
  (second (rest list))
