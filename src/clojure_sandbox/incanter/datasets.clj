(ns clojure-sandbox.datasets
  (:require [incanter.core :refer :all]
            [incanter.datasets :refer :all]
            [incanter.io :refer :all]))

; 1. Creating and viewing datasets

; Use a sample dataset included in incanter.datasets
(def iris (get-dataset :iris))
(def cars (get-dataset :cars))
(def hair-eyes (get-dataset :hair-eye-color))

; An incanter.core.Dataset has a friendly toString() implementation
iris
cars
hair-eyes

; view is a function that can be applied to lots of things including datasets (but later charts)
(view iris)

; There are various other ways to create a dataset
; ... from a sequence of column names and a sequence of rows
(dataset ["col1" "col2" "col3"]
         [[1 2 3]
          [4 5 6]
          [7 8 9]
          [8 7 6]])

; ... from a sequence of maps (column ordering might change)
(to-dataset [{:col1 1 :col2 2 :col3 3}
             {:col1 4 :col2 5 :col3 6}
             {:col1 7 :col2 8 :col3 9}])

; ... default column names are generated if not provided
(to-dataset [[1 2 3]
             [4 5 6]
             [7 8 9]])



; 2. Adding rows and columns

; you can conj columns
(conj-cols [1 4 7] [2 5 8] [3 6 9])

; ... or rows
(conj-rows [1 2 3] [4 5 6] [7 8 9])



; 3. Saving / loading datasets

; ... you can save an existing dataset with headers to a file
(save iris "./iris.csv" :header ["Sepal.Length" "Sepal.Width" "Petal.Length" "Petal.Width"
                                    "Species"])

; ... then load it in again
(def iris-copy (read-dataset "./iris.csv" :header true))
iris-copy