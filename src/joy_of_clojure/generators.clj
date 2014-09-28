(ns clojure-sandbox.joy-of-clojure.generators)

(def ascii
  (map char (range 65 91)))

(defn rand-str
  [sz alphabet]
  (apply str (repeatedly sz #(rand-nth alphabet))))

(def rand-sym
  #(symbol (rand-str %1 %2)))

(def rand-key
  #(keyword (rand-str %1 %2)))

(defn rand-vec
  [& generators]
  (into [] (map #(%) generators)))

(defn rand-map
  [sz kgen vgen]
  (into {}
        (repeatedly sz #(rand-vec kgen vgen))))