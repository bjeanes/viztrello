(ns viztrello.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [cemerick.friend :as friend]
            [environ.core :refer [env]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.session.cookie :refer [cookie-store]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.nested-params :refer [wrap-nested-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [viztrello.oauth.trello :as auth]
            [clojure.pprint :refer [pprint]]))

(defroutes app-routes
  (GET "/" [:as {session :session}]
       (let [identity (::friend/identity session)
             authentication ((:authentications identity) (:current identity))]
       (str "<pre>" (with-out-str (pprint authentication)) "</pre>")))
  (route/not-found "Not Found"))


(def app
  (-> app-routes
      (friend/wrap-authorize #{:user})
      (friend/authenticate {:credential-fn auth/creds->user
                            :workflows [(auth/workflow "VizTrello"
                                                       (env :trello-key)
                                                       (env :trello-secret))]})
      (wrap-keyword-params)
      (wrap-nested-params)
      (wrap-params)
      (wrap-session #_{:store (cookie-store {:key (env :session-key)})})
      (handler/site)))
