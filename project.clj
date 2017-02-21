(defproject clojure-sandbox "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [prismatic/plumbing "0.5.3"]
                 [prismatic/schema "1.1.3"]
                 [incanter/incanter "1.5.7"]
                 [org.clojure/algo.monads "0.1.6"]
                 [ring "1.5.1"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.5.2"]
                 [bidi "2.0.16"]
                 [metosin/ring-swagger-ui "2.2.8"]]
  :plugins [[lein-ancient "0.6.10"]]
  :repl-options {:port 4001})