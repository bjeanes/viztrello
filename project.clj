(defproject viztrello "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://viztrello.herokuapp.com"
  :dependencies [[clj-http "0.9.1"]
                 [clj-oauth "1.5.1" :exclusions [commons-codec]]
                 [com.cemerick/friend "0.2.0" :exclusions [org.openid4java/openid4java-nodeps
                                                           org.mindrot/jbcrypt
                                                           net.sourceforge.nekohtml/nekohtml
                                                           org.apache.httpcomponents/httpclient]]
                 [com.stuartsierra/component "0.2.1"]
                 [compojure "1.1.8" :exclusions [ring/ring-core]]
                 [clj-bonecp-url "0.1.1"]
                 [dorothy "0.0.5"]
                 [environ "0.5.0"]
                 [hiccup "1.0.5"]
                 [org.clojure/clojure "1.6.0"]
                 [org.clojure/java.jdbc "0.3.3"]
                 [org.slf4j/slf4j-simple "1.7.7"]
                 [postgresql "9.1-901-1.jdbc4"]
                 [ring/ring-devel "1.2.2"]
                 [ring/ring-jetty-adapter "1.2.2"]]
  :min-lein-version "2.0.0"
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.4"]
                                  [ring-mock  "0.1.5"]]}
             :uberjar {:aot :all}}
  :uberjar-name "viztrello-standalone.jar")
