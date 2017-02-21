(ns clojure-sandbox.grocery-store.yada
  (require [yada.yada :refer [listener resource as-resource]]))

(def svr
  (listener
    ["/"
     [
      ["hello" (as-resource "Hello World!")]
      ["test"  (resource {:produces "text/plain"
                          :response "This is a test!"})]
      [true (as-resource nil)]]]
    {:port 3000}))