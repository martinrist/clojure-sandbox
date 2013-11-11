(ns clojure-sandbox.number_classifier)

(defn factor? [number x]
  (zero? (mod number x)))

(defn factors [number]
 (filter (partial factor? number) (range 1 number)))

(defn sum-factors [number]
  (reduce + (factors number)))

(defn perfect? [number]
  (= (sum-factors number) number))