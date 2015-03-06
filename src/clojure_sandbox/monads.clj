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

(f 1)         ; => 2
;(f nil)      ; => Null Pointer Exception - we want nil



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






;; Part 2 - https://github.com/khinsen/monads-in-clojure/blob/master/PART2.md

; Another frequently-used monad is the sequence monad 9or list monad).  It's
; built into Clojure as the `for` form:

(for [a (range 5)
      b (range a)]
  (* a b))

; => (0 0 2 0 3 6 0 4 8 12)

; This is very similar to the `let` form, except `let` binds a single value
; to each symbol, whereas `for` binds several values in sequence.



; The monadic form of the above loop is written as:

(domonad sequence-m
         [a (range 5)
          b (range a)]
         (* a b))


; So, the questions is 'how do we write m-bind and m-result for the sequence
; monad?'

; m-bind calls a function of one argument that represents the rest of the
; computation, with the function argument representing the bound variable.
; Here the bound variable is a sequence, so an initial naive implementation
; might use `map`.

(defn m-bind-sequence-1
  [seq f]
  (map f seq))

(m-bind-sequence-1 (range 5) (fn [a]
(m-bind-sequence-1 (range a) (fn [b]
(* a b)))))

; => (() (0) (0 2) (0 3 6) (0 4 8 12))

; We have an extra level of sequence, that needs to get removed


(defn m-bind-sequence-2
  [seq f]
  (apply concat (map f seq)))

(m-bind-sequence-2 (range 5) (fn [a]
(m-bind-sequence-2 (range a) (fn [b]
(* a b)))))

; => IllegalArgumentException Don't know how to create ISeq from: java.lang.Long  clojure.lang.RT.seqFrom (RT.java:505)



; The problem here is that we can't apply `concat` to the result of applying
; `map f seq` where the return values of `f` are just primitive values:

(map (fn [b] (* 4 b)) (range 4))       ; => (0 4 8 12)
(apply concat '(0 4 8 12))             ; => IllegalArgumentException


; Instead we need to wrap each primitive return value in a single-value list:
(apply concat '( (0) (4) (8) (12) ))   ; => (0 4 8 12), as required



; We can do this using:

(m-bind-sequence-2 (range 5) (fn [a]
(m-bind-sequence-2 (range a) (fn [b]
(list (* a b))))))



; This is the sequence monad, where:

(defn m-bind-sequence
  [seq f]
  (apply concat (map f seq)))

(defn m-result-sequence
  [val]
  (list val))






; Terminology:
; - monadic value - [1 2 3]
; - monadic function - function that accepts a value and returns a monadic value
;
; m-bind's first argument must be a monadic value







; End of file comment to prevent "Cannot find surrounding form"
; on last form in file.
; (See: https://github.com/cursiveclojure/cursive/issues/750)