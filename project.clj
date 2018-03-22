(defproject dotty "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [http-kit "2.2.0"]
                 [ring/ring-defaults "0.3.1"]
                 [org.clojure/data.json "0.2.6"]]

  :main ^:skip-aot dotty.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
