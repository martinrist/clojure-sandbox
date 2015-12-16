(ns clojure-sandbox.grocery-store.yada
  (require [yada.yada :refer [yada swaggered]]
           [bidi.ring :refer [make-handler]]
           [ring.swagger.ui :as swag-ui]
           [aleph.http :refer [start-server]]))


(def hello
  (yada "Hello World!\n"))

(def not-found
  (fn [_] {:status 404 :body "Not found"}))

(def swag-api ["" [["/swagger-ui" (swag-ui/swagger-ui)]
                   [true not-found]]])

(def api
  ["" [["/swagger-ui" (swag-ui/swagger-ui)]
       ["/hello-api"
        (swaggered
          {:info {:title "Hello World!"
                  :version "1.0"
                  :description "Demonstrating yada + swagger"}
           :basePath "/hello-api"}
          ["/hello" hello])]
       [true not-found]]])




(comment

  (def aleph (start-server (make-handler api) {:port 3000}))
  (.close aleph)

  )