(ns viztrello.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [cemerick.friend :as friend]
            [clojure.java.io :as io]
            [environ.core :refer [env]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.session.cookie :refer [cookie-store]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [viztrello.oauth.trello :as auth]
            [clojure.pprint :refer [pprint]]))

(defroutes app-routes
  (GET "/" [:as {session :session}]
       (let [identity (::friend/identity session)
             authentication ((:authentications identity) (:current identity))]
       (str "<pre>" (with-out-str (pprint authentication)) "</pre>")))
  (route/not-found (slurp (io/resource "404.html"))))

(def app
  (-> #'app-routes
      (friend/wrap-authorize #{:user})
      (friend/authenticate {:credential-fn auth/creds->user
                            :workflows [(auth/workflow "VizTrello"
                                                       (env :trello-key)
                                                       (env :trello-secret))]})))

(defn wrap-error-page [handler]
  (fn [req]
    (try (handler req)
         (catch Exception e
           {:status 500
            :headers {"Content-Type" "text/html"}
            :body (slurp (io/resource "500.html"))}))))

(defn wrap-app [app]
  env
  (let [store (cookie-store {:key (env :session-secret)})]
    (-> app
        ((if (env :production)
           wrap-error-page
           #(wrap-stacktrace (wrap-reload %))))
        (site {:session {:store store}}))))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (wrap-app #'app) {:port port :join? false})))
