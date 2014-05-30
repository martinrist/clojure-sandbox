(ns clojure-sandbox.incanter.charts
  (:require [incanter.core :refer :all]
            [incanter.datasets :refer :all]
            [incanter.charts :refer :all]
            [incanter.pdf :refer :all]
            [incanter.optimize :refer :all]))

(def iris (get-dataset :iris))



; 1. Scatter Plots
; ... create plot using scatter-plot, passing x and y col names
(scatter-plot :Sepal.Length :Sepal.Width :data iris)

; ... view can open charts as well as tables...
(view (scatter-plot :Sepal.Length :Sepal.Width :data iris))

; ... or you can save it as a PNG
(save (scatter-plot :Sepal.Length :Sepal.Width :data iris) "./scatter.png")

(save-pdf (scatter-plot :Sepal.Length :Sepal.Width :data iris) "./scatter.pdf")

; ... charts can have titles, x-labels and y-labels
(view (scatter-plot :Sepal.Length :Sepal.Width
                    :data iris
                    :title "Iris Scatter Plot"
                    :x-label "Sepal Length (cm)"
                    :y-label "Sepal Width (cm)"))

; ... and we can group by another column in the dataset
(view (scatter-plot :Sepal.Length :Sepal.Width
                    :data iris
                    :title "Iris Scatter Plot by Species"
                    :x-label "Sepal Length (cm)"
                    :y-label "Sepal Widrh (cm)"
                    :group-by :Species))



; 2. Bar & line charts
; ... Better with hair & eye colour dataset (more dimensions)
(def hair-eye-color (get-dataset :hair-eye-color))

; ... mean count of people by hair & eye colour.
; ':legend true' adds a colour legend
(with-data ($rollup :mean :count [:hair :eye] hair-eye-color)
           (view (bar-chart :hair :count
                            :group-by :eye
                            :title "Mean count of people by hair & eye colour"
                            :legend true)))

; ... line charts are similar to bar charts
(with-data ($rollup :mean :count [:hair :eye] hair-eye-color)
           (view (line-chart :hair :count
                            :group-by :eye
                            :title "Mean count of people by hair & eye colour"
                            :legend true)))



; 3. Histograms
; ... Basic histogram with default number of bins (10)
(view (histogram :Petal.Length
                 :data iris
                 :title "Distribution of petal length"))

; ... Increase number of bins
(view (histogram :Petal.Length
                 :data iris
                 :title "Distribution of petal length"
                 :nbins 20))



; 4. Function Plots
; ... can plot a generic single-arg function across a range
(defn f [x] (* x x (sin x)))
(view (function-plot f -20 20))


; ... adding multiple functions onto a single chart
(-> (function-plot f -20 20)
    (add-function (comp - f) -20 20)
    view)



; 5. Others
; ... box-plot
(view (box-plot :Petal.Width
                :data iris
                :group-by :Species
                :title "Box plot of Petal Width by Species"))

; ... pie chart
(with-data ($rollup :sum :count :hair hair-eye-color)
           (view (pie-chart :hair
                            :count
                            :title "Count of people by hair colour")))

; ... heat-map
(view (heat-map (fn [x y] (sin (sqrt (+ (sq x) (sq y)))))
                -10 10
                -10 10
                :title "Heat map of sin(r)"))


; ... and many more ...