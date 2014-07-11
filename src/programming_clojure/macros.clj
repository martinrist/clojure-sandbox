(ns programming-clojure.macros)

(defmacro unless
  [expr & body]
  (list 'if expr nil (cons 'do body)))

(defmacro chain
  ([x form]
   `(. ~x ~form))

  ([x form & more]
   `(chain (. ~x ~form) ~@more)))