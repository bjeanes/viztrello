(ns viztrello.web
  (:require [com.stuartsierra.component :as component]
            [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [cemerick.friend :as friend]
            [clojure.java.io :as io]
            [environ.core :refer [env]]
            [hiccup.page :refer (html5)]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.session.cookie :refer [cookie-store]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [viztrello.trello :as trello]
            [friend.oauth.trello :as auth]))

(defn- req->creds [req]
  "Given a request, return the Trello credentials saved in the session"
  (let [identity (::friend/identity (:session req))
        current-user ((:authentications identity) (:current identity))]
    (:credentials current-user)))

(defn- cards->html [cards]
  [:ul
   (for [card cards]
     [:li [:a {:href (str "/c/" (:shortLink card))} (:name card)]])])

(defn layout
  [r & content]
  (let [identity (::friend/identity (:session r))
        current-user ((:authentications identity) (:current identity))]
    (html5
      [:head
       [:title "VizTrello"]]
      [:body
       (cons
         [:p (str "Hi @" (-> current-user :trello :username))]
         content)])))

(defroutes app-routes
  (GET "/" r
       (layout r [:form {:action "/search"}
                  [:input {:name "query"}]
                  [:input {:type "submit" :value "Search"}]]))
  (GET "/search" [query :as r]
       (layout r
               [:h1 "Pick a card to display dependencies"]
               (cards->html (:cards (trello/search (req->creds r) query)))))
  (GET "/c/:id" [id :as r]
       (layout r
               ))
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
