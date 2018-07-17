(ns clojure-data-science.chapter-1.data
  (:require [clojure.java.io :as io]
            [incanter.core :as i]
            [incanter.excel :as xls]
            [incanter.stats :as s]
            [incanter.charts :as c]
            [incanter.distributions :as d]))


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
  (let [x-bar            (mean xs)
        n                (count xs)
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



;; The Normal Distribution


(defn ex-1-15 []
  "Plot a histogram of a uniform distribution"
  (let [xs (->> (repeatedly rand)
                (take 10000))]
    (-> (c/histogram xs
                     :x-label "Uniform distribution"
                     :nbins 20)
        i/view)))

(defn ex-1-16 []
  "Plot a histogram of the distribution of means"
  (let [xs (->> (repeatedly rand)
                (partition 10)
                (map mean)
                (take 10000))]
    (-> (c/histogram xs
                     :x-label "Distribution of means"
                     :nbins 20)
        i/view)))

(defn ex-1-17 []
  (let [distribution (d/normal-distribution)
        xs           (->> (repeatedly #(d/draw distribution))
                          (take 10000))]
    (-> (c/histogram xs
                     :x-label "Normal distribution"
                     :nbins 20)
        i/view)))



;; Poincaré's Baker

(defn honest-baker [mean sd]
  "An honest baker who distributes loaves according to a normal distribution"
  (let [distribution (d/normal-distribution mean sd)]
    (repeatedly #(d/draw distribution))))

(defn ex-1-18 []
  "Plot a histogram of bread weights from an honest baker"
  (-> (take 10000 (honest-baker 1000 30))
      (c/histogram :x-label "Honest baker"
                   :nbins 25)
      i/view))

(defn dishonest-baker [mean sd]
  "A dishonest baker who only distributes the heaviest of each baker's dozen"
  (let [distribution (d/normal-distribution mean sd)]
    (->> (repeatedly #(d/draw distribution))
         (partition 13)
         (map (partial apply max)))))

(defn ex-1-19 []
  "Plot a histogram of bread weights from a dishonest baker"
  (-> (take 10000 (dishonest-baker 950 30))
      (c/histogram :x-label "Dishonest baker"
                   :nbins 25)
      i/view))



;; Skewness

(defn ex-1-20 []
  "Display statistics about the dishonest baker"
  (let [weights (take 10000 (dishonest-baker 950 30))]
    {:mean     (mean weights)
     :median   (median weights)
     :skewness (s/skewness weights)}))

(defn ex-1-21 []
  "Plot the Q-Q plot for the honest baker, plotting actual quartiles against those
   for a normal distribution"
  (->> (honest-baker 1000 30)
       (take 10000)
       c/qq-plot
       i/view))

(defn ex-1-22 []
  "Plot the Q-Q plot for the dishonest baker.  The curve indicates positive
   skew in the distribution"
  (->> (dishonest-baker 950 30)
       (take 10000)
       c/qq-plot
       i/view))



;; Comparative Visualisations

(defn ex-1-22 []
  "Plot a box-and-whisker plot for the honest and dishonest bakers"
  (-> (c/box-plot (->> (honest-baker 1000 30)
                       (take 10000))
                  :legend true
                  :y-label "Loaf weight (g)"
                  :series-label "Honest baker")
      (c/add-box-plot (->> (dishonest-baker 950 30)
                           (take 10000))
                      :series-label "Dishonest baker")
      i/view))



(defn ex-1-23 []
  "Plot distribution against CDF for the honest and dishonest bakers"
  (let [sample-honest    (->> (honest-baker 1000 30)
                              (take 1000))
        sample-dishonest (->> (dishonest-baker 950 30)
                              (take 1000))
        ecdf-honest      (s/cdf-empirical sample-honest)
        ecdf-dishonest   (s/cdf-empirical sample-dishonest)]
    (-> (c/xy-plot sample-honest (map ecdf-honest sample-honest)
                   :x-label "Loaf Weight"
                   :y-label "Probability"
                   :legend true
                   :series-label "Honest baker")
        (c/add-lines sample-dishonest
                     (map ecdf-dishonest sample-dishonest)
                     :series-label "Dishonest baker")
        i/view)))



;; Visualising Electoral Data

(defn ex-1-24 []
  "Plot fitted and empirical CDFs for electorate data"
  (let [electorate (->> (load-data :uk-scrubbed)
                        (i/$ "Electorate"))
        ecdf (s/cdf-empirical electorate)
        fitted (s/cdf-normal electorate
                             :mean (s/mean electorate)
                             :sd   (s/sd electorate))]
    (-> (c/xy-plot electorate fitted
                   :x-label "Electorate"
                   :y-label "Probability"
                   :series-label "Fitted"
                   :legend true)
        (c/add-lines electorate (map ecdf electorate)
                     :series-label "Empirical")
        i/view)))

(defn ex-1-25 []
  "Show a Q-Q plot which shows the left-skew (i.e. smaller constituencies)"
  (->> (load-data :uk-scrubbed)
       (i/$ "Electorate")
       c/qq-plot
       i/view))



;; Adding Columns

; There are various ways of changing columns in Incanter:
; - `i/replace-column` - replace contents of column with sequence
; - `i/transform-column` - replace contents of column by applying function
; - `i/add-column` - add new column with sequence
; - `i/add-derived-column` - add new column by applying function

(defn ex-1-26 []
  "Attempt to derive `:victors` by adding values.  This fails because of some non-numeric data"
  (->> (load-data :uk-scrubbed)
       (i/add-derived-column :victors [:Con :LD] +)))

(defn ex-1-27 []
  "Show records with blank values for 'Con' or 'LD'"
  (->> (load-data :uk-scrubbed)
       (i/$where #(not-any? number? [(% "Con") (% "LD")]))
       (i/$ [:Region :Electorate :Con :LD])))

(defmethod load-data :uk-victors [_]
  (->> (load-data :uk-scrubbed)
       (i/$where {:Con {:$fn number?} :LD {:$fn number?}})
       (i/add-derived-column :victors [:Con :LD] +)
       (i/add-derived-column :victors-share [:victors :Votes] /)
       (i/add-derived-column :turnout [:Votes :Electorate] /)))

(defn ex-1-28 []
  "Plot a QQ-plot of the 'Victors Share', which illustrates 'light tails'
  vs a normal distribution"
  (->> (load-data :uk-victors)
       (i/$ :victors-share)
       c/qq-plot
       i/view))



;; Comparative Visualisastions of Electorate Data

(defmethod load-data :ru [_]
  (i/conj-rows (-> (io/resource "clojure_data_science/chapter_1/Russia2011_1of2.xls")
                   str
                   xls/read-xls)
               (-> (io/resource "clojure_data_science/chapter_1/Russia2011_2of2.xls")
                   str
                   xls/read-xls)))

(defn ex-1-29 []
  "Show column names for Russian electoral data"
  (-> (load-data :ru)
      (i/col-names)))

(defmethod load-data :ru-victors [_]
  (->> (load-data :ru)
       (i/rename-cols
         {"Number of voters included in voters list" :electorate
          "Number of valid ballots" :valid-ballots
          "United Russia" :victors})
       (i/add-derived-column :victors-share
                             [:victors :valid-ballots] i/safe-div)
       (i/add-derived-column :turnout
                             [:valid-ballots :electorate] /)))

; This is time-consuming, so just load it once
(defonce ru-victors (load-data :ru-victors))



;; Visualising Russian election data

(defn ex-1-30 []
  "Plot a histogram of Russian turnout data.  These show high
  positive skew and an increase in turnout from 80% - 100%"
  (-> (i/$ :turnout ru-victors)
      (c/histogram :x-label "Russia turnout"
                   :nbins 20)
      i/view))

(defn ex-1-31 []
  "Plot a QQ-plot for Russian turnout data.  This shows a light tail at
  the top, and a heavy tail at the bottom, which is different from what
  we see in the histogram."
  (->> ru-victors
       (i/$ :turnout)
       c/qq-plot
       i/view))



;; Probability Mass Function

; The PMF is a function similar to a histogram, but it plots the probability
; that a value from the distribution will be exactly equal to a given value.

; As a result, the total area under the graph will be equal to 1, which means
; it's good for comparing data

(defn as-pmf [bins]
  "Normalises the values to fall between 0 and 1"
  (let [histogram (frequencies bins)
        total     (reduce + (vals histogram))]
    (->> histogram
         (map (fn [[k v]]
                [k (/ v total)]))
         (into {}))))

(defn ex-1-32 []
  "Plot PMFs for UK and Russian turnout data"
  (let [n-bins 40
        uk (->> (load-data :uk-victors)
                (i/$ :turnout)
                (bin n-bins)
                as-pmf)
        ru (->> ru-victors
                (i/$ :turnout)
                (bin n-bins)
                as-pmf)]
    (-> (c/xy-plot (keys uk) (vals uk)
                   :series-label "UK"
                   :legend true
                   :x-label "Turnout Bins"
                   :y-label "Probability")
        (c/add-lines (keys ru) (vals ru)
                     :series-label :Russia)
        i/view)))



;; Scatter Plots

(defn ex-1-33 []
  "Plot victor's share vs turnout for UK electoral data"
  (let [data (load-data :uk-victors)]
    (-> (c/scatter-plot (i/$ :turnout data)
                        (i/$ :victors-share data)
                        :x-label "Turnout"
                        :y-label "Victor's Share")
        i/view)))

(defn ex-1-34 []
  "Plot victor's share vs turnout for Russian electoral data"
  (let [data ru-victors]
    (-> (c/scatter-plot (i/$ :turnout data)
                        (i/$ :victors-share data)
                        :x-label "Turnout"
                        :y-label "Victor's Share")
        (c/set-alpha 0.05)
        i/view)))