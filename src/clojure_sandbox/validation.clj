(ns clojure-sandbox.validation
  (:require [clojure.algo.monads :refer :all]))


(defn size-valid?
  [coll limit]
  (println "Validating size of" coll)
  (let [size (count coll)]
    (<= size limit)))

(defn contents-valid?
  [coll]
  (println "Validating contents of" coll)
  (every? pos? coll))


(defn process
  [coll]
  (if (size-valid? coll 5)
    (if (contents-valid? coll)
      (println "Contents are all valid" coll)
      (println "Something in" coll "isn't valid"))
    (println coll "is too large")))


(defn default-false
  [coll]
  [false coll])

(defn default-true
  [coll]
  [true coll])


(defn validate-size
  [[valid? coll] limit]
  (if valid?
    (let [size (count coll)]
      (println "Validating size of" coll "against limit of" limit)
      (if (<= size limit)
        [true coll]
        [false coll]))
    [false coll]))

(defn validate-contents
  [[valid? coll] pred]
  (if valid?
    (do
      (println "Validating contents of" coll)
      (if (every? pred coll)
        [true coll]
        [false coll]))
    [false coll]))

(defn what-i-want
  [coll]
  (-> coll
      default-true
      (validate-size 5)
      (validate-contents pos?)
      (println coll)))

(defn validate-not-empty
  "Validates that `coll` is not empty"
  [coll]
  (println "Checking collection is not empty")
  (if (empty? coll)
    [nil "Collection cannot be empty"]
    [coll nil]))

(defn validate-size
  "Validate that the size of `coll` is not more than `limit`"
  [limit coll]
  (println "Checking collection is not too large")
  (let [size (count coll)]
    (if (> size limit)
      [nil (str "Collection has more elements than allowed (" size ">" limit ")")]
      [coll nil])))

(def validate-size-5 (partial validate-size 5))

(defn validate-contents
  "Validate that all the members of `coll` satisfy `pred`"
  [pred coll]
  (println "Checking members of collection")
  (if (every? pred coll)
    [coll nil]
    [nil "Validation failed for a member of the collection"]))

(def validate-contents-pos (partial validate-contents pos?))


(defn validate-and-perform-1
  "First attempt at chaining validations together.  Note the repeated pattern
  of (if (nil? err) (...) [nil err]"
  [coll]
  (let [[coll err] (validate-not-empty coll)
        [coll err] (if (nil? err) (validate-size-5 coll) [nil err])
        [coll err] (if (nil? err) (validate-contents-pos coll) [nil err])]
    (if (nil? err)
      (println "Collection passed all validation: " coll)
      (println "Validation failed: " err))))

(defn bind-error
  [[val err] f]
  (if (nil? err)
    (f val)
    [nil err]))

(defn validate-and-perform-2
  "Second attempt at chaining validation, after pulling out apply-or-error"
  [coll]
  (let [result (validate-not-empty coll)
        result (bind-error result validate-size-5)
        [val err] (bind-error result validate-contents-pos)]
    (if (nil? err)
      (println "Collection passed all validation: " val)
      (println "Validation failed: " err))))


(defn validate-and-perform-3
  "Third attempt at chaining validation - as above but using ->>"
  [coll]
  (let [[val err] (-> coll
                       validate-not-empty
                       (bind-error validate-size-5)
                       (bind-error validate-contents-pos))]
    (if (nil? err)
      (println "Collection passed all validation: " val)
      (println "Validation failed: " err))))


; Holy shit - here come the macros
(defmacro err->>
  [val & fns]
  (let [fns (for [f fns] `(bind-error ~f))]
    `(-> [~val nil]
          ~@fns)))


(defn validate-and-perform-4
  "Fourth attempt at chaining validation using err->>"
  [coll]
  (let [[val err] (err->> coll
                          validate-not-empty
                          validate-size-5
                          validate-contents-pos)]
    (if (nil? err)
      (println "Collection passed all validation: " val)
      (println "Validation failed: " err))))



; Validation using an error monad.
; The monadic type takes the form of a tuple [val err].
;
; Each validation function is therefore a monadic function from the raw value to
; the tuple [val err]

(defn result-error
  [val]
  [val nil])

(defn bind-error
  [[val err] f]
  (if (nil? err)
    (f val)
    [nil err]))


(defmonad error-m
          [m-result   result-error
           m-bind     bind-error])


(defn validate-and-perform-5
  [coll]
  (domonad error-m
           [coll  (validate-not-empty coll)
            coll  (validate-size 5 coll)
            coll  (validate-contents pos? coll)]
           coll))



(def validate-and-perform-6
  (with-monad error-m
              (m-chain [validate-not-empty
                        validate-size-5
                        validate-contents-pos])))