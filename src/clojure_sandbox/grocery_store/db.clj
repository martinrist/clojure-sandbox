(ns clojure-sandbox.grocery-store.db)

(def fruit-data
  {"apples"  {:name    "Granny Smith Apples"
              :price   1.50
              :stock   20
              :updated #inst "2015-01-01T12:00:00"}
   "bananas" {:name    "Slightly Green Bananas"
              :price   0.70
              :stock   25
              :updated #inst "2015-02-01T12:00:00"}
   "oranges" {:name    "Blood Oranges"
              :price   2.00
              :stock   15
              :updated #inst "2015-03-01T12:00:00"}}
  )

(def store-db
  (atom
    {:fruits fruit-data}))


(defn get-all-fruits
  "Returns a map of all known fruits in `db`, indexed by key"
  [db]
  (:fruits @db))

(defn get-fruit
  "Returns a map that represents a single fruit in `db`, whose key value is `key`.
  Returns `nil` if no fruit with the specified `key` can be found."
  [db key]
  (get (:fruits @db) key nil))

(defn add-fruit!
  "Adds `fruit` into `db`, with the specified `key`."
  [db key fruit]
  (swap! db assoc-in [:fruits key] fruit))

(defn update-fruit!
  "Updates the fruit record in `db` with the specified `key`.  Sets the new
  values of the fruit to be those in `fruit`.  Missing keys are not updated.
  Has no action if `key` does not exist in `db`."
  [db key deltas]
  (if (get-fruit db key)
    (swap! db update-in [:fruits key] #(merge % deltas))))