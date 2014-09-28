(ns clojure-sandbox.joy-of-clojure.event-sourcing
  "Event sourcing exmaples from Section 14.3 of 'The Joy of Clojure")

(defn valid?
  "Determines if `event` is a valid event for the baseball batting model."
  [event]
  (boolean (:result event)))

(defn effect
  "Applies `event` to the current state, returning the new state.
   State is a map of:
   `:ab`  - number of 'at bats'.
   `:h`   - number of hits.
   `:avg` - batting average."
  [{:keys [ab h] :or {ab 0, h 0}} event]
  (let [ab    (inc ab)
        h     (if (= :hit (:result event))
                (inc h)
                h)
        avg   (double (/ h ab))]
    {:ab ab :h h :avg avg}))

(defn apply-effect
  "Apply `event` to `state` if valid, returning the new state."
  [state event]
  (if (valid? event)
    (effect state event)
    state))

(def effect-all
  "Takes a state and a sequence of events, and applies all
  valid events to the state.  Returns the state after applying
  events."
  #(reduce apply-effect %1 %2))