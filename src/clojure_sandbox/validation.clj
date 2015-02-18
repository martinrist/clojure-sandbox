(ns clojure-sandbox.validation)


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
    (println coll "is too large"))

  )


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