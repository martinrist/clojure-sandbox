(ns clojure-sandbox.grocery-store.api
  (:require [compojure.core :refer :all]
            [ring.util.response :refer [response status not-found created]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [schema.core :as s]
            [clojure-sandbox.grocery-store.db :as db]
            [clojure-sandbox.grocery-store.model :as model]))

(defroutes fruit-handler
           (GET "/fruits/:key" [key]
             (if-let [fruit (db/get-fruit db/store-db key)]
               (response fruit)
               (not-found {:message (str key " not found")})))
           (PUT "/fruits/:key" [key :as {body :body}]
             (if-let [errors (s/check model/update-fruit-req-schema body)]
               (-> {:error (pr-str errors)}
                   response
                   (status 400))
               (do
                 (db/update-fruit! db/store-db key body)
                 (db/get-fruit db/store-db key))))
           )

(defroutes fruits-handler
           (GET "/fruits" [] (response (db/get-all-fruits db/store-db)))
           (POST "/fruits" {body :body}
             (let [key (:key body)
                   fruit (dissoc body :key)]
               (if-let [errors (s/check model/add-fruit-req-schema body)]
                 (-> {:error (pr-str errors)}
                     response
                     (status 400))
                 (do
                   (db/add-fruit! db/store-db key fruit)
                   (created key)))))
           (ANY "/fruits" [] (-> (response "Method not allowed")
                                 (status 405))))


(defroutes all-handlers
           fruit-handler
           fruits-handler)



(def app
  (-> all-handlers
      wrap-json-response
      (wrap-json-body {:keywords? true})))


(comment
  "Jetty server"
  (use 'ring.adapter.jetty)

  (def jetty (run-jetty app {:port 3000 :join? false}))

  (.stop jetty)
  (.start jetty)

  )