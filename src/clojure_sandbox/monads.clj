(ns clojure-sandbox.monads
  (:require [clojure.algo.monads :refer :all]))



; Part 1 - https://github.com/khinsen/monads-in-clojure/blob/master/PART1.md

; Example of a simple 'let' binding.
; First 1 is bound to `a`, then `inc a` is bound to `b`, then `(* a b)` is
; evaluated and returned.

(let [a 1
      b (inc a)]
  (* a b))



; If Clojure didn't have a 'let' form, this could be expressed using functions.
; Ick.  The functions appear in the wrong order, and there are a lot of
; parens, even for a Lisp.

((fn [a]
   ((fn [b]
      (* a b)) (inc a))) 1)



; In a first attempt to clean this up, define a helper function 'bind'
; that 'reverses' a value and function:

(defn bind
  [val f]
  (f val))



; This allows us to rewrite the earlier example as something that looks
; a little closer to the nice 'let' form:

(bind 1       (fn [a]
(bind (inc a) (fn [b]
      (* a b)))))







(domonad identity-m
         [a 1
          b (inc a)]
         (* a b))




; End of file comment to prevent "Cannot find surrounding form"
; on last form in file.
; (See: https://github.com/cursiveclojure/cursive/issues/750)