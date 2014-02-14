(ns clojure-sandbox.number-classifier-test
  (:use clojure.test
        clojure-sandbox.number_classifier))

(deftest testFactors
  (testing "factors of 10"
    (is (= (list 1 2 5) (factors 10)))
    )
  (testing "factors of 1"
    (is (= () (factors 1)))
    )
  )


