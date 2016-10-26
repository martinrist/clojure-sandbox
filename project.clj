(defproject clojure-sandbox "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha13"]
                 [prismatic/plumbing "0.5.2"]
                 [prismatic/schema "1.0.4"]
                 [incanter/incanter "1.5.5"]
                 [org.clojure/algo.monads "0.1.5"]
                 [ring "1.4.0"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.4.0"]
                 [yada "1.0.0-20150828.203727-8"]
                 [aleph "0.4.0"]
                 [bidi "1.23.1"]
                 [metosin/ring-swagger-ui "2.1.3-4"]]
  :repl-options {:port 4001})