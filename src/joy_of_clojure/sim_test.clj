(ns joy-of-clojure.sim-test
  (:require [clojure-sandbox.joy-of-clojure.event-sourcing :as es]
            [clojure-sandbox.joy-of-clojure.generators :as gen]
            [clojure.set :as sql]))

(def PLAYERS #{{:player "Nick" :ability 32/100}
               {:player "Matt" :ability 26/100}
               {:player "Ryan" :ability 19/100}})

(defn lookup
  [db name]
  (first (sql/select
           #(= name (:player %))
           db)))

(defn update-stats
  [db event]
  (let [player      (lookup db (:player event))
        less-db     (sql/difference db #{player})]
    (conj less-db
          (merge player (es/effect player event)))))

(defn commit-event
  [db event]
  (dosync
    (alter db update-stats event)))

(defn rand-event
  [{ability :ability}]
  (let [able (numerator ability)
        max  (denominator ability)]
    (gen/rand-map 1
                  #(-> :result)
                  #(if (< (rand-int max) able)
                    :hit
                    :out))))

(defn rand-events
  [total player]
  (take total
        (repeatedly #(assoc (rand-event player)
                      :player
                      (:player player)))))

(def agent-for-player
  (memoize
    (fn [player-name]
      (let [a  (agent [])]
        (set-error-handler! a #(println "ERROR: " %1 %2))
        (set-error-mode! a :fail)
        a))))

(defn feed
  [db event]
  (let [a (agent-for-player (:player event))]
    (send a
          (fn [state]
            (commit-event db event)
            (conj state event)))))

(defn feed-all
  [db events]
  (doseq [event events]
    (feed db event))
  db)

(defn simulate
  [total players]
  (let [events (apply interleave
                      (for [player players]
                        (rand-events total player)))
        results (feed-all (ref players) events)]
    (apply await (map #(agent-for-player (:player %)) players))
    @results))