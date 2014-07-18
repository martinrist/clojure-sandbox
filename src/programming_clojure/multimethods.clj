(ns programming-clojure.multimethods
  (:import (clojure.lang PersistentVector IPersistentVector)
           (java.util Collection))
  (:require [clojure.string :as str]))

(defmulti my-print class)

(defmethod my-print String
           [s]
  (.write *out* s))

(defmethod my-print nil
           [s]
  (my-print "nil"))

(defmethod my-print Number
           [n]
  (my-print (str "Number: " (.toString n))))

(defmethod my-print Integer
           [i]
  (my-print (str "Integer: " (.toString i)))
  )
(defmethod my-print Collection
           [coll]
  (my-print "(")
  (my-print (str/join " " coll))
  (my-print ")"))

(defmethod my-print IPersistentVector
           [vec]
  (my-print "[")
  (my-print (str/join " " vec))
  (my-print "]"))

(defmethod my-print :default
           [s]
  (my-print (str "#<" (.toString s) "#>")))

