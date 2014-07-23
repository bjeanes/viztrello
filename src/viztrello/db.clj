(ns viztrello.db
  (:require [com.stuartsierra.component :as component]
            [clojure.java.jdbc :as jdbc]
            [clj-bonecp-url.core :refer (datasource-from-url)]
            [cheshire.core :as json])
  (:import org.postgresql.util.PGobject))

(defrecord DB [uri]
  component/Lifecycle

  (start [this]
    (if (:connection this)
      this
      (assoc this :connection {:datasource (datasource-from-url uri)})))

  (stop [this]
    (if-let [connection (:connection this)]
      (do (.close (:datasource connection)) (dissoc this :connection))
      this)))

(defn new [uri] (map->DB {:uri uri}))

;; JSON column support
;;   from http://hiim.tv/clojure/2014/05/15/clojure-postgres-json/

(defn value-to-json-pgobject [value]
  (doto (PGobject.)
    (.setType "json")
      (.setValue (json/generate-string value))))

(extend-protocol jdbc/ISQLValue
  clojure.lang.IPersistentMap
  (sql-value [value] (value-to-json-pgobject value))

  clojure.lang.IPersistentVector
  (sql-value [value] (value-to-json-pgobject value)))

(extend-protocol jdbc/IResultSetReadColumn
  PGobject
  (result-set-read-column [pgobj metadata idx]
    (let [type  (.getType pgobj)
          value (.getValue pgobj)]
      (case type
        "json" (json/parse-string value :key-fn keyword)
        :else value))))
