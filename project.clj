(defproject viztrello "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://viztrello.herokuapp.com"
  :dependencies [[clj-oauth "1.5.1"]
                 [com.cemerick/friend "0.2.0" :exclusions [org.openid4java/openid4java-nodeps
                                                           org.mindrot/jbcrypt
                                                           net.sourceforge.nekohtml/nekohtml
                                                           org.apache.httpcomponents/httpclient]]
                 [compojure "1.1.8"]
                 [environ "0.5.0"]
                 [org.clojure/clojure "1.6.0"]
                 [ring/ring-jetty-adapter "1.2.2"]
                 [ring/ring-devel "1.2.2"]
                 [trello "0.1.2-SNAPSHOT"]]
  :min-lein-version "2.0.0"
  :uberjar-name "viztrello-standalone.jar")
