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

(defn lazy-seq-fibo
  "Version of fibo that uses the lazy-seq macro to delay generation of values"
  ([]
    (concat [0 1] (lazy-seq-fibo 0N 1N)))
  ([a b]
   (let [n (+ a b)]
     (lazy-seq
       (cons n (lazy-seq-fibo b n))))))

(defn iterate-fibo
  "Version of fibo that uses core sequence library functions"
  []
  (map first (iterate (fn [[a b]] [b (+ a b)]) [0N 1N])))