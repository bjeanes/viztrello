(defproject viztrello "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://viztrello.herokuapp.com"
  :dependencies [[clj-http "0.9.1"]
                 [clj-oauth "1.5.1" :exclusions [commons-codec]]
                 [com.cemerick/friend "0.2.0" :exclusions [org.openid4java/openid4java-nodeps
                                                           org.mindrot/jbcrypt
                                                           net.sourceforge.nekohtml/nekohtml
                                                           org.apache.httpcomponents/httpclient]]
                 [compojure "1.1.8" :exclusions [ring/ring-core]]
                 [environ "0.5.0"]
                 [org.clojure/clojure "1.6.0"]
                 [ring/ring-devel "1.2.2"]
                 [ring/ring-jetty-adapter "1.2.2"]]
  :min-lein-version "2.0.0"
  :profiles {:uberjar {:aot :all}}
  :uberjar-name "viztrello-standalone.jar")
