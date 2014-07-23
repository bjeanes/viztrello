(ns viztrello.db.migrate
  (:require [viztrello.db :as db]
            [environ.core :refer (env)]
            [com.stuartsierra.component :refer (start stop)]
            [clojure.java.jdbc :as jdbc]))

(def ^:private migrations
  (sorted-map-by #(compare (:added (meta %1))
                           (:added (meta %2)))

                 ^{:added #inst "2014-05-20T00:03:46-07:00"}
                 'setup-migrations (fn [] "some sql")))


(defn- setup? [db]
  (-> (jdbc/query db
                  "SELECT EXISTS(
                  SELECT *
                  FROM information_schema.tables
                  WHERE table_schema = 'public'
                  AND table_name = 'schema_migrations')")
      first
      :exists))

(defn- setup! [db]
  (when-not (setup? db)
    (jdbc/db-do-commands
      db
      (jdbc/create-table-ddl :schema_migrations
                             [:migration :text "PRIMARY KEY"]
                             [:migrated_at :timestamp "DEFAULT now() NOT NULL"]))))

(defn- migrated? [db migration]
  (-> (jdbc/query db ["SELECT EXISTS(
                      SELECT *
                      FROM schema_migrations
                      WHERE migration = ?)"
                      (str migration)])
      first
      :exists))

(defn- record-migration [migration])

(defn- migrate!
  [db]
  (println "Running migrations...")
  (jdbc/with-db-transaction [tx db]
    (setup! tx)

    (for [[migration migrate] migrations]
      (when-not (migrated? tx migration)
        (println "Migrating" migration)
        (jdbc/db-do-commands tx
                             (migrate)
                             (record-migration migration))))))

(defn -main []
  (let [db (start (db/new (env :database-url)))]
    (try
      (migrate! (:connection db))
      (finally
        (stop db)))))
