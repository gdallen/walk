(defproject walk "0.1.0-SNAPSHOT"
            :description "Walk:  a Clojure noir app to figure out how far you walked in the park"
            :dependencies [[org.clojure/clojure "1.3.0"]
                           [noir "1.3.0-beta8"]
                           [crate "0.2.0-alpha4"]
                           [jayq "0.1.0-alpha4"]
                           [fetch "0.1.0-alpha2"]
                           [hiccup "1.0.0"]
                           ;[crate "0.1.0-SNAPSHOT"]
                           ;[jayq "0.1.0-SNAPSHOT"]
                           ;[fetch "0.1.0-SNAPSHOT"]
                          ]
            :dev-dependencies [[lein-cljsbuild "0.0.13"]]
            :cljsbuild {:source-path "src-cljs"
                        :compiler
                          {:output-dir "resources/public/cljs/"
                           :output-to "resources/public/cljs/main.js"
                           :optimizations :simple
                           :pretty-print true}}
            :main walk.server)

