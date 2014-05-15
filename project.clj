(defproject viztrello "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[clj-oauth "1.5.1"]
                 [com.cemerick/friend "0.2.0" :exclusions [org.openid4java/openid4java-nodeps
                                                           org.mindrot/jbcrypt
                                                           net.sourceforge.nekohtml/nekohtml
                                                           org.apache.httpcomponents/httpclient]]
                 [com.stuartsierra/component "0.2.1"]
                 [compojure "1.1.8"]
                 [dorothy "0.0.5"]
                 [environ "0.5.0"]
                 [org.clojure/clojure "1.6.0"]
                 [trello "0.1.2-SNAPSHOT"]]
  :plugins [[lein-ring "0.8.10"]]
  :ring {:handler viztrello.handler/app}
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [ring-mock "0.1.5"]]}})
