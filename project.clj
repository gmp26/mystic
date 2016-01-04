(defproject mystic "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [devcards "0.2.1"]
                 [rum "0.6.0"]
                 #_[sablono "0.4.0"]
                 #_[org.omcljs/om "0.9.0"]
                 #_[reagent "0.5.1"]]

  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-figwheel "0.5.0-1"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]

  :source-paths ["src"]

  :cljsbuild {
              :builds [{:id "devcards"
                        :source-paths ["src"]
                        :figwheel { :devcards true } ;; <- note this
                        :compiler { :main       "mystic.cards"
                                    :asset-path "js/compiled/devcards_out"
                                    :output-to  "resources/public/js/compiled/mystic_devcards.js"
                                    :output-dir "resources/public/js/compiled/devcards_out"
                                    :source-map-timestamp true }}
                       {:id "dev"
                        :source-paths ["src"]
                        :figwheel true
                        :compiler {:main       "mystic.core"
                                   :asset-path "js/compiled/out"
                                   :output-to  "resources/public/js/compiled/mystic.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :source-map-timestamp true }}
                       {:id "prod"
                        :source-paths ["src"]
                        :compiler {:main       "mystic.core"
                                   :asset-path "js/compiled/out"
                                   :output-to  "resources/public/js/compiled/mystic.js"
                                   :optimizations :advanced}}]}

  :figwheel { :css-dirs ["resources/public/css"] })
