(ns viztrello.web
  (:require [com.stuartsierra.component :as component]
            [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
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
            [friend.oauth.trello :as auth]
            [clojure.pprint :refer [pprint]]))

(defroutes app-routes
  (GET "/" [:as {session :session}]
       (let [identity (::friend/identity session)
             authentication ((:authentications identity) (:current identity))]
         (str "<pre>" (with-out-str (pprint authentication)) "</pre>")))
  (route/not-found (slurp (io/resource "404.html"))))

(def authed-app
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

(defrecord App [port]
  component/Lifecycle

  (start [this]
    (if (:server this)
      this
      (assoc this :server
             (jetty/run-jetty (wrap-app #'authed-app)
                              {:port port :join? false}))))

  (stop [this]
    (if-let [server (:server this)]
      (do (.stop server)
          (dissoc this :server))
      this)))

(defn new-app [port]
  (map->App {:port port}))
