(ns clojure-sandbox.grocery-store.model
  (require [schema.core :as s]))

(def fruit-schema
  {:name                     s/Str
   :price                    s/Num
   :stock                    s/Int
   (s/optional-key :updated) s/Inst})

(def add-fruit-req-schema
  (assoc fruit-schema :key s/Str))


(defn mapkeys
  "Return map `m`, with each key transformed by function `f`"
  [m f]
  (into {} (concat (for [[k v] m]
                     [(f k) v]))))

(defn make-optional
  [k]
  (if (keyword? k)
    (s/optional-key k)
    k))

(def update-fruit-req-schema
  (mapkeys fruit-schema make-optional))
