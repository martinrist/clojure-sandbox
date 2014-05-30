(ns clojure-sandbox.incanter.data_manipulation
  (:require [incanter.core :refer :all]
            [incanter.datasets :refer :all]
            [incanter.io :refer :all]
            [incanter.stats :refer :all]))

(def iris (get-dataset :iris))



; 1. Column selection using $ (short for sel)

; ... single column returns a LazySeq of the values in that column
($ :Sepal.Length iris)

; ... multiple columns returns a Dataset
($ [:Sepal.Length :Sepal.Width] iris)

; ... :not allows excluding of columns
($ [:not :Species] iris)




; 2. Filtering Datasets using $where (short for query-dataset)
; ... by single row criterion
($where {:Species "versicolor"} iris)

; ... using :in
($where {:Species {:in #{"versicolor" "virginica"}}} iris)

; ... using various shorthand predicates
($where {:Petal.Width {:gt 1.0 :lt 1.5}} iris)

; ... by multiple row criteria
($where {:Species "virginica"
         :Petal.Width {:gt 1.0 :lt 1.5}} iris)

; ... or an arbitrary predicate
($where #(.endsWith ^String (:Species %) "a") iris)



; 3. Sorting using $order
; ... sorting by single column
($order :Sepal.Length :asc iris)

; ... descending order
($order :Sepal.Length :desc iris)

; ... using multiple columns
($order [:Sepal.Length :Sepal.Width] :asc iris)




; 4. Summarising data using $rollup

; Basic (and advanced stats functions are in incanter.stats
(mean ($ :Sepal.Length iris))
(sd ($ :Sepal.Length iris))

; ... using keywords for standard functions
($rollup :mean :Petal.Length :Species iris)

; ... or functions in incanter.stats
($rollup sd :Petal.Length :Species iris)

; ... like the Pearson chi-squared test value for matching Petal Length values against
; Benford's Law...
($rollup benford-test :Petal.Length :Species iris)

; ... or custom functions ('Standard Error' is Std Dev / Count)
($rollup #(/ (sd %) (count %)) :Petal.Length :Species iris)



; 5. with-data Macro
; Binds the given data to $data and executes the body (useful for $, $where and other functions
; that use $data if no dataset is provided)
(with-data iris
           {:mean (mean ($ :Sepal.Length))
            :sd   (sd ($ :Sepal.Length))})

(with-data iris
           ($where {:Petal.Length {:gt 6}}))