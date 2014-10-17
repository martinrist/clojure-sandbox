(ns clojure-sandbox.joy-of-clojure.reducers)

(defn empty-range?
  [start end step]
  (or (and (pos? step) (>= start end))
      (and (neg? step) (<= start end))))

(defn lazy-range
  "Returns a lazy seq equivalent to `(range start end step)`"
  [start end step]
  (lazy-seq
    (if (empty-range? start end step)
      nil
      (cons start
            (lazy-range (+ start step)
                        end
                        step)))))

(defn reducible-range
  "Reducible constructor that creates a reducible with the
  same values as `(range start end step)`."
  [start end step]
  (fn [reducing-fn initial-value]
    (loop [result initial-value
           i      start]
      (if (empty-range? i end step)
        result
        (recur (reducing-fn result i)
               (+ i step))))))

(defn half
  "Mapping function that halves values"
  [x]
  (/ x 2))

(defn half-transformer
  "Reducing function transformer that takes `reducing-fn` and
  returns another reducing function that pre-applies `half`"
  [reducing-fn]
  (fn [result input]
    (reducing-fn result (half input))))

(defn mapping
  "Transformer constructor that returns a reducing function
  transformer that applies `map-fn`."
  [map-fn]
  (fn map-transform [reducing-fn]
    (fn [result input]
      (reducing-fn result (map-fn input)))))

(defn filtering
  "Transformer constructor that returns a reducing function
  that filters according to `pred`"
  [filter-pred]
  (fn filter-transform [reducing-fn]
    (fn [result input]
      (if (filter-pred input)
        (reducing-fn result input)
        result))))

(defn mapcatting
  "Transformer constructor that captures the essence of the
  map-cat operation.  `map-fn` is expected to return a reducible."
  [map-fn]
  (fn [reducing-fn]
    (fn [result input]
      (let [reducible (map-fn input)]
        (reducible reducing-fn result)))))

(defn r-map
  "Reducible transformer that converts `reducible` into a new
  reducible whose reducing function is passed through the
  reducing function transformer `(mapping mapping-fn)`."
  [mapping-fn reducible]
  (fn new-reducible [reducing-fn init]
    (reducible ((mapping mapping-fn) reducing-fn) init)))

(defn r-filter
  "Reducible transformer that converts `reducible` into a new
  reducible whose reducing function is passed through the
  reducing function transformer `(filtering filter-pred)`."
  [filter-pred reducible]
  (fn new-reducible [reducing-fn init]
    (reducible ((filtering filter-pred) reducing-fn) init)))