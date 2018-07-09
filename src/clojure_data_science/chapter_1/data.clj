(ns clojure-data-science.chapter-1.data
  (:require [clojure.java.io :as io]
            [incanter.core :as i]
            [incanter.excel :as xls]
            [incanter.stats :as s]
            [incanter.charts :as c]))


;;; Inspecting the Data

(defmulti load-data identity)

(defmethod load-data :uk [_]
  (-> (io/resource "clojure_data_science/chapter_1/UK2010.xls")
      (str)
      (xls/read-xls)))

(def uk-data (load-data :uk))

(defn ex-1-1 []
  "Return the names of the columns in the dataset"
  (i/col-names uk-data))

(defn ex-1-2 []
  "Return the values in a specified column"
  (i/$ "Election Year" uk-data))

(defn ex-1-3 []
  "Get distinct values from 'Election Year"
  (->> uk-data
       (i/$ "Election Year")
       distinct))

(defn ex-1-4 []
  "Get frequencies of values in 'Election Year'"
  (->> uk-data
       (i/$ "Election Year")
       frequencies))



;; Data Scrubbing

(defn ex-1-5 []
  "Query for nil 'Election Year' values"
  (->> uk-data
       (i/$where {"Election Year" {:$eq nil}})
       i/to-map))

(defmethod load-data :uk-scrubbed [_]
  "Get a 'scrubbed' version of UK data with non-nil 'Election Year'"
  (->> (load-data :uk)
       (i/$where {"Election Year" {:$ne nil}})))

(defn ex-1-6 []
  "Get the count of constituencies"
  (->> (load-data :uk-scrubbed)
       (i/$ "Electorate")
       count))


;; Descriptive Statistics

(defn mean [xs]
  "Calculate the mean of the values in `xs`"
  (/ (reduce + xs)
     (count xs)))

(defn ex-1-7 []
  "Get the mean constituency size - also available as `s/mean`"
  (->> (load-data :uk-scrubbed)
       (i/$ "Electorate")
       mean))

(defn median [xs]
  "Calculate the median of the values in `xs`"
  (let [n   (count xs)
        mid (int (/ n 2))]
    (if (odd? n)
      (nth (sort xs) mid)
      (->> (sort xs)
           (drop (dec mid))
           (take 2)
           mean))))

(defn ex-1-8 []
  "Get the median constituency size - also available as `s/median`"
  (->> (load-data :uk-scrubbed)
       (i/$ "Electorate")
       median))



;; Variance

(defn variance [xs]
  "Calculate the variance of the values in `xs` - also available as `s/variance`"
  (let [x-bar (mean xs)
        n     (count xs)
        square-deviation (fn [x]
                           (i/sq (- x x-bar)))]
    (mean (map square-deviation xs))))

(defn standard-deviation [xs]
  "Calculate the standard deviation of the values in `xs` - also available as `s/sd`"
  (i/sqrt (variance xs)))

(defn ex-1-9 []
  "Get the standard deviation of constituency size"
  (->> (load-data :uk-scrubbed)
       (i/$ "Electorate")
       standard-deviation))



;; Quantiles

(defn quantile [q xs]
  "Calculate the quantiles for the values in `xs` - also available as `s/quantile`"
  (let [n (dec (count xs))
        i (-> (* n q)
              (+ 1/2)
              int)]
    (nth (sort xs) i)))

(defn ex-1-10 []
  "Get the quantiles of constituency size"
  (let [xs (->> (load-data :uk-scrubbed)
                (i/$ "Electorate"))
        f  (fn [q]
             (quantile q xs))]
    (map f [0 1/4 1/2 3/4 1])))

(defn ex-1-10-2 []
  "Get the quantiles of constituency size - using `s/quantile`"
  (let [xs (->> (load-data :uk-scrubbed)
                (i/$ "Electorate"))]
    (s/quantile xs :probs [0 1/4 1/2 3/4 1])))



;; Binning Data

(defn bin [n-bins xs]
  "Assign the values in `xs` into one of `n-bins` bins"
  (let [min-x   (apply min xs)
        max-x   (apply max xs)
        range-x (- max-x min-x)
        bin-fn  (fn [x]
                  (-> x
                      (- min-x)
                      (/ range-x)
                      (* n-bins)
                      int
                      (min (dec n-bins))))]
    (map bin-fn xs)))

(defn ex-1-11 []
  (->> (load-data :uk-scrubbed)
       (i/$ "Electorate")
       (bin 5)
       frequencies))



;; Histograms

(def uk-electorate (->> (load-data :uk-scrubbed)
                        (i/$ "Electorate")))

(defn ex-1-12 []
  "Draw a histogram of constituency size"
  (-> uk-electorate
      c/histogram
      i/view))

(defn ex-1-13 []
  "Draw a histogram of constituency size with more bars"
  (-> uk-electorate
      (c/histogram :nbins 200)
      i/view))

(defn ex-1-14 []
  "Draw a histogram of constituency size with 20 bars"
  (-> uk-electorate
      (c/histogram :x-label "UK Electorate"
                   :nbins 20)
      i/view))
