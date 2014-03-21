(ns clojure-sandbox.executors
  (:import (java.util.concurrent Executors ScheduledExecutorService TimeUnit Future)))

(def counter (atom 0))

(defn inc-counter
  []
  (do
    (reset! counter (inc @counter))
    (println (str "Incrementing counter to " @counter))))

(defn dec-counter
  []
  (do
    (reset! counter (dec @counter))
    (println (str "Decrementing counter to " @counter))))

(def executor-service (Executors/newSingleThreadScheduledExecutor))

(defn schedule
  "Create and execute a periodic action which will call `fn` every `rate` milliseconds."
  [fn rate]
  (.scheduleAtFixedRate ^ScheduledExecutorService executor-service fn 0 rate TimeUnit/MILLISECONDS))

(defn cancel
  [future]
  (.cancel ^Future future true))
