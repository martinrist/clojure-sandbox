(ns clojure-sandbox.edn
  (:require [clojure.edn :as edn]
            [clojure-sandbox.units :refer :all]))

(def input "#uuid \"dae78a90-d491-11e2-8b8b-0800200c9a66\"")

(def readers {'unit/length #'clojure-sandbox.units/distance-reader})