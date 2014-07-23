(ns viztrello.trello
  (:require [clj-http.client :as http]))

(defn- GET [path opts]
  (let [api "https://trello.com/1"]
    (let [opts (-> opts
                   (assoc :as :json)
                   (update-in [:query-params]
                              merge (:credentials opts))
                   (dissoc :credentials))]
      (http/get (str api path) opts))))

(defn search
  [creds query]

  (:body (GET "/search"
              {:credentials creds
               :query-params {:query query
                              :modelTypes "cards"
                              :card_fields "name,shortLink"
                              :cards_limit 50}})))

(defn card
  [creds id]

  (:body (GET (str "/cards/" id)
              {:credentials creds
               :query-params {:actions ""
                              :fields "name,desc"}})))
