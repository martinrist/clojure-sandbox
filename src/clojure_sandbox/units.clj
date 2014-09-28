(ns clojure-sandbox.units)

(def simple-metric {:meter 1,
                    :metre 1,
                    :m 1,
                    :km 1000,
                    :cm 1/100,
                    :mm [1/10 :cm]})

(defn convert
  [context descriptor]
  (reduce (fn [result [mag unit]]
            (+ result
               (let [val (get context unit)]
                 (if (vector? val)
                   (* mag (convert context val))
                   (* mag val)))))
          0
          (partition 2 descriptor)))

(def distance-reader
  (partial convert simple-metric))

(def simple-time {:sec 1
                  :min 60,
                  :hr  [60 :min],
                  :day [24 :hr]})

(def time-reader
  (partial convert
           simple-time))