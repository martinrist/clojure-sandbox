(ns programming-clojure.functional-programming)

(defn stack-consuming-fibo
  "Attempts to calculate fib(n) using naive recursion, which blows the stack"
  [n]
  (cond
    (= n 0) 0
    (= n 1) 1
    :else (+ (stack-consuming-fibo (- n 1))
             (stack-consuming-fibo (- n 2)))))