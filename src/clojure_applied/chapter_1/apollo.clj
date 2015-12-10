(ns clojure-applied.chapter-1.apollo
  "Contains model definitions and functions to represent Apollo space
  missions.

  Illustrates various uses of map destructuring to pass optional args
  ")



(defrecord Mission [name system launched manned?            ; Mandatory attributes
                    cm-name lm-name orbits evas             ; Optional attributes
                    ])


(defn make-mission-optattrs-as-map
  "Factory method to create `Mission` instances.
  Optional attributes are passed in as a map."
  [name system launched manned? opts]
  (let [{:keys [cm-name lm-name orbits evas]} opts]
    (->Mission name system launched manned? cm-name lm-name orbits evas)))

(def mission-defaults {:orbits 0 :evas 0})

(defn make-mission-optattrs-as-map-with-defaults
  "Factory method to create `Mission` instances.
  Optional attributes are passed in as a map, but are defaulted."
  [name system launched manned? opts]
  (let [{:keys [cm-name lm-name orbits evas]} (merge mission-defaults opts)]
    (->Mission name system launched manned? cm-name lm-name orbits evas)))

(defn make-mission-optattrs-as-varargs
  "Factory method to create `Mission` instances.
  Optional attributes are passed in as varargs (key-value pairs)."
  [name system launched manned? & opts]
  (let [{:keys [cm-name lm-name orbits evas]} opts]
    (->Mission name system launched manned? cm-name lm-name orbits evas)))

(defn make-mission-opt-attrs-as-varags-with-defaults
  "Factory method to create `Mission` instances.
  Optional attributes are passed in as varargs (key-value pairs).  Missing
  attributes are defaulted."
  [name system launched manned? & opts]
  (let [{:keys [cm-name lm-name orbits evas] :or {orbits 0 evas 0}} opts]
    (->Mission name system launched manned? cm-name lm-name orbits evas)))



(comment

  "Any unspecified optional attributes are just `nil`."
  (def apollo-4 (make-mission-optattrs-as-map
                  "Apollo 4"
                  "Saturn V"
                  #inst "1967-11-09T12:00:01-00:00"
                  false
                  {:orbits 3}))

  "Here, the default value of `:evas` is taken from `mission-defaults`."
  (def apollo-4' (make-mission-optattrs-as-map-with-defaults
                   "Apollo 4"
                   "Saturn V"
                   #inst "1967-11-09T12:00:01-00:00"
                   false
                   {:orbits 3}))

  "Optional arguments are passed as key-value pairs, rather than in a map."
  (def apollo-11 (make-mission-optattrs-as-varargs
                   "Apollo 11"
                   "Saturn V"
                   #inst "1969-07-16T13:32:00-00:00"
                   true
                   :cm-name "Columbia"
                   :lm-name "Eagle"
                   :orbits 30
                   :evas 1))

  "Optional arguments are passed in as key-value pairs, with defaulting."
  (def apollo-4'' (make-mission-opt-attrs-as-varags-with-defaults
                    "Apollo 4"
                    "Saturn V"
                    #inst "1967-11-09T12:00:01-00:00"
                    false
                    :orbits 3))

  )