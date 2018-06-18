(defproject clojure-sandbox "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [prismatic/plumbing "0.5.5"]
                 [prismatic/schema "1.1.9"]
                 [incanter/incanter "1.9.3"]
                 [org.clojure/algo.monads "0.1.6"]
                 [ring "1.6.3"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.6.1"]
                 [bidi "2.1.3"]
                 [metosin/ring-swagger-ui "2.2.8"]]
  :plugins [[lein-ancient "0.6.15"]]
  :repl-options {:port 4001})