(ns clojure-sandbox.spec
  (:require [clojure.spec.alpha :as s]))

; Use `s/def` to define a registry of some basic specs
(s/def ::date inst?)
(s/def ::suit #{:heart :club :diamond :spade})


(s/def ::big-even (s/and int? even? #(> % 1000)))


; Specs used for entity map demonstrations
(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")
(s/def ::email-type (s/and string? #(re-matches email-regex %)))
(s/def ::acctid int?)
(s/def ::first-name string?)
(s/def ::last-name string?)
(s/def ::email ::email-type)
(s/def ::phone (s/nilable string?))

; Person spec with qualified keys
(s/def ::person (s/keys :req [::first-name ::last-name ::email]
                        :opt [::phone]))


; Person spec with unqualified keys
(s/def :unq/person (s/keys :req-un [::first-name ::last-name ::email]
                           :opt-un [::phone]))

(defrecord Person [first-name last-name email phone])


; Using a multi-spec to support a 'type' tag on maps

; `:event/type` is a tag for the type of the event
; Specs will use this value as a way of determining the validation rules to apply
(s/def :event/type keyword?)
(defmulti event-type :event/type)

; `:event/timestamp` is a common key, used by all events
(s/def :event/timestamp int?)

; events of type `:event/search` require the common and `:search/url` keys
(s/def :search/url string?)
(defmethod event-type :event/search [_]
  (s/keys :req [:event/type :event/timestamp :search/url]))

; events of type `:event/message` require the common and two `:error/` keys
(s/def :error/message string?)
(s/def :error/code int?)
(defmethod event-type :event/error [_]
  (s/keys :req [:event/type :event/timestamp :error/code :error/message]))

(s/def :event/event (s/multi-spec event-type :event/type))


; An example of using spec for validation, wiht pre- and post-conditions
(defn person-name
  [person]
  {:pre  [(s/valid? ::person person)]
   :post [(s/valid? string? %)]}
  (str (::first-name person " " (::last-name person))))


; An example of using `fdef` to specify a function with specs
; First the function definition...
(defn ranged-rand
  "Returns random int in range start <= rand < end"
  [start end]
  (+ start (long (rand (- end start)))))

; ... then the spec details, which can be declared separately
(s/fdef ranged-rand
        :args   (s/and (s/cat :start int? :end int?)
                       #(< (:start %) (:end %)))
        :ret    int?
        :fn     (s/and #(>= (:ret % (-> % :args :start)))
                       #(< (:ret % (-> % :args :end)))))

; Specs can be written for higher-order-functions
; `adder` is a HOF which returns a function which adds `x`
(defn adder [x] #(+ x %))

; We can declare a function spec for `adder` using `s/fspec`
(s/fdef adder
        :args   (s/cat :x number?)
        :ret    (s/fspec :args (s/cat :y number?)
                         :ret number?)
        :fn     #(= (-> % :args :x) ((:ret %) 0)))


;; A more complete set of specs to model a game of cards
(def suit? #{:heart :club :diamond :spade})
(def rank? (into #{:jack :queen :king :ace} (range 2 11)))
(def deck (for [suit suit? rank rank?] [rank suit]))

(s/def ::card (s/tuple rank? suit?))
(s/def ::hand (s/* ::card))

(s/def ::name string?)
(s/def ::score int?)
(s/def ::player (s/keys :req [::name ::score ::hand]))

(s/def ::players (s/* ::player))
(s/def ::deck (s/* ::card))
(s/def ::game (s/keys :req [::players ::deck]))

(defn total-cards
  "Counts up the total number of cards afer the deal"
  [{:keys [::deck ::players] :as game}]
  (apply + (count deck)
         (map #(-> % ::hand count) players)))

(defn deal
  [game]
  ; Implementation omitted
  )

; Function spec that enforces that the total number of
; cards must be the same before and after dealing
(s/fdef deal
        :args (s/cat :game ::game)
        :ret  ::game
        :fn   #(= (total-cards (-> % :args :game))
                  (total-cards (-> % :ret))))