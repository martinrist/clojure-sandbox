(ns clojure-sandbox.number_classifier)

(defn factor? [number x]
  (zero? (mod number x)))

(defn factor-of-number? [number]
    (fn [x] (factor? number x)))

; TODO: Work out a way of using partial to improve this and remove the need for factor-of-number
(defn factors [number]
 (filter (factor-of-number? number) (range 1 number)))

(defn sum-factors [number]
  (reduce + (factors number)))

(defn perfect? [number]
  (= (sum-factors number) number))