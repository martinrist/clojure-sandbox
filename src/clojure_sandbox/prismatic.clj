(ns clojure-sandbox.prismatic
  (:require [plumbing.core :refer :all]
            [plumbing.graph :as graph]
            [plumbing.fnk.pfnk :as pfnk]))

(defn stats
  "Takes a map {:xs xs} and return a map of simple statistics on xs"
  [{:keys [xs] :as m}]
  (assert  (contains? m :xs))
  (let [n  (count xs)
        m  (/ (sum identity xs) n)
        m2 (/ (sum #(* % %) xs) n)
        v  (- m2 (* m m))]
    {:n  n  ; count
     :m  m  ; mean
     :m2 m2 ; mean squared
     :v  v  ; variance
     }))


(declare xs n m m2)   ; for IntelliJ

(def stats-graph
  {:n    (fnk [xs]    (doseq [] (println (str "Calculating n in thread " (Thread/currentThread))) (count xs)))
   :m    (fnk [xs n]  (doseq [] (println (str "Calculating m in thread " (Thread/currentThread))) (/ (sum identity xs) n)))
   :m2   (fnk [xs n]  (doseq [] (println (str "Calculating m2 in thread " (Thread/currentThread))) (/ (sum #(* % %) xs) n)))
   :v    (fnk [m m2]  (doseq [] (println (str "Calculating v in thread " (Thread/currentThread))) (- m2 (* m m))))
   })

(def stats-eager (graph/eager-compile stats-graph))
(def stats-lazy (graph/lazy-compile stats-graph))
(def stats-parallel (graph/par-compile stats-graph))

(def extended-stats-graph
  (assoc stats-graph :sd (fnk [v] (Math/sqrt v))))

(def extended-stats-eager (graph/eager-compile extended-stats-graph))

(def extended-stats-lazy (graph/lazy-compile extended-stats-graph))