(ns clojure-sandbox.monads
  (:require [clojure.algo.monads :refer :all]))


; For IntelliJ
(declare a b)


;; Part 1 - https://github.com/khinsen/monads-in-clojure/blob/master/PART1.md

; Example of a simple 'let' binding.
; First 1 is bound to `a`, then `inc a` is bound to `b`, then `(* a b)` is
; evaluated and returned.

(let [a 1
      b (inc a)]
  (* a b))                                                  ; (1)



; If Clojure didn't have a 'let' form, this could be expressed using functions.
; Ick.  The functions appear in the wrong order, and there are a lot of
; parens, even for a Lisp.

((fn [a]
   ((fn [b]
      (* a b)) (inc a))) 1)                                 ; (2)



; In a first attempt to clean this up, define a helper function 'bind'
; that 'reverses' a value and function:

(defn m-bind-identity
  [val f]
  (f val))                                                  ; (3)



; This allows us to rewrite the earlier example as something that looks
; a little closer to the nice 'let' form (1).

(m-bind-identity 1       (fn [a]
(m-bind-identity (inc a) (fn [b]
                 (* a b)))))                                ; (4)



; Finally, we use the `domonad` macro to turn this into something that
; looks almost exactly like the `let` form (1)
; `identity-m` is the 'Identity' monad, defined in `clojure.algo.monads`

(domonad identity-m
         [a 1
          b (inc a)]
         (* a b))                                           ; (5)



; Using macroexpand-1 on (5) gives:
(clojure.algo.monads/with-monad identity-m
    (m-bind   1       (fn [a]
    (m-bind   (inc a) (fn [b]
    (m-result (* a b)))))))                                 ; (6)

; which is almost exactly (4), with the following differences:
;
; - It's run within `with-monad identity-m` to specify evaluation within the identity monad.
; - s/bind/m-bind/, since we're using the m-bind operation from identity-m
; - Addition of `m-result`, which for identity-m is just `identity`

; So, monads are generalisations of the 'let' form that replace the simple
; m-bind and m-result functions with something more complex.  Each monad is
; defined by an implemetnation of m-bind and m-result.



; Next example - suppose we have computations that can fail, signalling failure
; by returning `nil`.  So, if the result of any of the following steps is `nil`
; (e.g. x is nil), the result is `nil` and no subsequent steps are run.

(defn f
  [x]
  (let [a x
        b (inc a)]
    (* a b)))

(f 1)        ; => 2
(f nil)      ; => Null Pointer Exception - we want nil



; Here we can use the 'Maybe' monad

(defn f'
  [x]
  (domonad maybe-m
           [a   x
            b   (inc a)]
           (* a b)))

(f' 1)        ; => 2, as before
(f' nil)      ; => nil, as required



; The m-bind function for maybe-m is:

(defn m-bind-maybe
  [val f]
  (if (nil? val)
    nil
    (f val)))




; End of file comment to prevent "Cannot find surrounding form"
; on last form in file.
; (See: https://github.com/cursiveclojure/cursive/issues/750)