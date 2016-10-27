(ns clojure-sandbox.spec
  (:require [clojure.spec :as s]))

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
