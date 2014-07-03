(ns programming-clojure.macros)

(defmacro unless
  [expr & body]
  (list 'if expr nil (cons 'do body)))

(defmacro chain
  ([x form]
   (list '. x form))

  ([x form & more]
   (concat (list 'chain (list '. x form)) more)))