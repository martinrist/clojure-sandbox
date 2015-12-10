(ns clojure-applied.chapter-1.money
  "Contains model definitions and functions to work with a representation
  of monetary values as suggested by Martin Fowler in 'Patterns of Enterprise
  Application Architecture'.

  Illustrates use of '& rest'-style args and variadic functions to implement
  common arithmetic functions for `Money` instances."
  )


; Basic currency record and common classes
(defrecord Currency [divisor sym desc])

(def currencies {:usd (->Currency 100 "USD" "US Dollars")
                 :eur (->Currency 100 "EUR" "Euro")
                 :gbp (->Currency 100 "GBP" "UK Pound")})


; Validation of currency matching
; Should this really be done by throwing exceptions?
(defn- validate-same-currency
  [m1 m2]
  (or (= (:currency m1) (:currency m2))
      (throw
        (ex-info "Currencies do not match."
                 {:m1 m1 :m2 m2}))))


; Money record.  Note how we also implement Comparable, which is then used
; in `=$`
(defrecord Money [amount ^Currency currency]
  Comparable
  (compareTo [m1 m2]
      (validate-same-currency m1 m2)
      (compare (:amount m1) (:amount m2))))


(defn =$
  "Checks equality of one or more `Money` instances.  A single instance is
  considered trivially equal to itself."
  ([_] true)
  ([m1 m2] (zero? (.compareTo m1 m2)))
  ([m1 m2 & monies]
    (every? zero? (map #(.compareTo m1 %) (conj monies m2)))))

(defn +$
  "Returns the sum of one or more `Money` instances"
  ([m1] m1)
  ([m1 m2]
    (validate-same-currency m1 m2)
    (->Money (+ (:amount m1) (:amount m2)) (:currency m1)))
  ([m1 m2 & monies]
    (reduce +$ m1 (conj monies m2))))

(defn *$
  [m n]
  "Multiplies the `Money` instance `m` by the scalar value `n`, retaining the
  same currency."
  (->Money (* n (:amount m)) (:currency m)))

(defn make-money
  "Factory methods to create `Money` instances with appropriate defaults."
  ([]                 (make-money 0))
  ([amount]           (make-money amount (:usd currencies)))
  ([amount currency]  (->Money amount currency)))


(comment

  "Now we can call `make-money` with various defaults"
  (make-money)                                              ; 0 USD
  (make-money 100)                                          ; 100 USD
  (make-money 100 (:gbp currencies))                        ; 100 GBP


  )
