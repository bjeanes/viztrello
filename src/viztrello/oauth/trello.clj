(ns viztrello.oauth.trello
  (:require [viztrello.oauth :as oauth]
            [clj-http.client :as http]))

(def ^:private endpoints
  {:request-uri "https://trello.com/1/OAuthGetRequestToken"
   :access-uri "https://trello.com/1/OAuthGetAccessToken"
   :authorize-uri "https://trello.com/1/OAuthAuthorizeToken"})

(defn creds->user
  [credentials]
  (let [query-params {:fields "fullName,initials,url,username,timezoneInfo,email"}
        query-params (merge query-params credentials)]
    (let [me (http/get "https://api.trello.com/1/members/me"
                       {:as :json
                        :query-params query-params
                        :throw-exceptions false})]
      (when (= (:status me) 200)
        {:identity (-> me :body :id)
         :roles [:user]
         :trello (:body me)
         :credentials credentials}))))

(defn workflow
  [app-name key secret]
  (let [config (merge endpoints {:key key :secret secret})
        config (update-in config [:authorize-uri] str "?name=" app-name)]
    (oauth/workflow {::oauth/consumer (oauth/make-consumer config)})))
