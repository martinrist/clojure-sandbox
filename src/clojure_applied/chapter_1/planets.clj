(ns clojure-sandbox.planets)

; Simple map definition.  The :type value of :planet could be used, for example,
; to filter planetary objects by 'type'
(def earth {:name       "Earth"
            :moons      1
            :volume     1.08321e12                          ;; km^3
            :mass       5.97219e24                          ;; kg
            :aphelion   152098232                           ;; km, farthest from sun
            :perihelion 147098290                           ;; km, closest to sun
            :type       :planet
            })

(def sol    {:name      "Sol"
             :planets   9
             :volume    1.41e18                             ;; km^3
             :mass      1.989e30                            ;; kg
             :type      :star
             })


(comment

  "This is how we use :type to filter planets"
  (filter #(= :planet (:type %)) [earth sol])

  )


; Records provide more class-like features
(defrecord Planet [name moons volume mass aphelion perihelion])

; Like positional and mpa-based factory functions
(def earth-rec (->Planet "Earth" 1 1.08321e12 5.97219e24 152098232 147098290))
(def earth-rec' (map->Planet earth))


(comment

  "`earth`'s type is just `clojure.lang.PersistentArrayMap"
  (type earth)

  "Whereas `earth-rec`'s is `clojure_sandbox.chapter_1.Planet"
  (type earth-rec)

  "You can do `instance?` tests on `earth-rec`"
  (instance? Planet earth-rec)

  )