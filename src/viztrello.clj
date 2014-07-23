(ns viztrello
  (:require [com.stuartsierra.component :as component]
            [viztrello.db :as db]
            [viztrello.web :as web]
            [environ.core :refer [env]]))

(defn system []
  (-> (component/system-map
        :db (db/new (env :database-url))
        :app (web/new-app (Integer. (env :port 5000))))
      (component/system-using
        {:app [:db]})))

(defn -main []
  (let [system (component/start (system))]
    (-> (Runtime/getRuntime)
        (.addShutdownHook (Thread. #(component/stop system))))))
