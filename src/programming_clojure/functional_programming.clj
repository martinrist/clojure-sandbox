(ns programming-clojure.functional-programming)

(defn stack-consuming-fibo
  "Attempts to calculate fib(n) using naive recursion, which blows the stack"
  [n]
  (cond
    (= n 0) 0
    (= n 1) 1
    :else (+ (stack-consuming-fibo (- n 1))
             (stack-consuming-fibo (- n 2)))))

(defn tail-fibo
  "'Improves' stack-consuming-fibo by moving recursive call into tail position.
  Note that this doesn't make any difference, due to lack of automatic TCO in the JVM."
  [n]
  (letfn [(fib
            [current next n]
            (if (zero? n)
              current
              (fib next (+ current next) (dec n))))]
    (fib 0N 1N n))
  )

(defn recur-fibo
  "Version of tail-fibo which using explicit self-recursion via `recur`"
  [n]
  (letfn [(fib
            [current next n]
            (if (zero? n)
              current
              (recur next (+ current next) (dec n))))]
    (fib 0N 1N n))
  )