(ns friend.oauth
  (:require [oauth.client :as oauth]
            [clj-http.client :refer [generate-query-string]]
            [ring.util.response :refer [response status redirect]]
            [ring.util.request :as request]
            [cemerick.friend :as friend]
            [cemerick.friend.workflows :as wf])
  (:import org.apache.http.client.utils.URIBuilder))

(defn make-consumer
  [{:keys [key secret request-uri
           access-uri authorize-uri]}]
  (oauth/make-consumer key secret request-uri access-uri authorize-uri :hmac-sha1))

(defn- user-approval-uri
  [consumer request-token]
  (-> (URIBuilder. (:authorize-uri consumer))
      (.addParameter "oauth_token" (:oauth_token request-token))
      (.toString)))

(defn- callback-uri
  [{scheme :scheme {host "host"} :headers :as req}]
  (let [login-uri (-> req ::friend/auth-config :login-uri)
        callback-path (str login-uri "/callback")]
    (str (name scheme) "://" host callback-path)))

(defn- redirect-to-provider-for-access!
  [consumer callback-uri]

  (let [request-token (oauth/request-token consumer callback-uri)]
    (update-in (redirect (user-approval-uri consumer request-token))
           [:session] assoc ::request-token request-token)))

(defn- login?
  [request]
  (= (request/path-info request)
     (-> request ::friend/auth-config :login-uri)))

(defn- callback?
  [request]
  (let [login-uri (-> request ::friend/auth-config :login-uri)
        callback-uri (str login-uri "/callback")]
    (= (request/path-info request) callback-uri)))

(defn- handle-callback
  [request config consumer]
  (let [request-token (get-in request [:session ::request-token])
        oauth-token (get-in request [:params :oauth_token])
        oauth-verifier (get-in request [:params :oauth_verifier])]

    (when (= oauth-token (:oauth_token request-token))
      (when-let [access-token (oauth/access-token consumer
                                                  request-token
                                                  oauth-verifier)]
        (let [credentials {:token (:oauth_token access-token)
                           :key (:key consumer)}
              credential-fn (:credential-fn config)]
          (when-let [user-record (credential-fn credentials)]
            (wf/make-auth user-record {::friend/workflow :oauth})))))))

(defn workflow
  [config]

  (let [consumer (::consumer config)]
    (fn [request]
      (let [config (merge config (-> request ::friend/auth-config))]
        (cond
          (login? request)    (redirect-to-provider-for-access! consumer (callback-uri request))
          (callback? request) (handle-callback request config consumer))))))

