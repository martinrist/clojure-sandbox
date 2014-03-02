(ns programming-clojure.sequences)

(def my-list '(1 2 3 4 5))
(def my-vec [1 2 3 4 5])
(def my-set #{50 40 30 20 10})
(def my-sorted-set (into (sorted-set) my-set))
(def my-map {:a 1 :b 2 :c 3 :d 4 :e 5})

(def integers (iterate inc 0))

(defn integers-less-than
  [n]
  (take-while #(< % n) integers))