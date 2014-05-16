(ns user
  "Helpers and `require`s available only in dev (e.g. in REPL).

  See http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded
  and https://github.com/stuartsierra/component for the general idea."

  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [com.stuartsierra.component :as component]
            [viztrello :as app]))

(def system nil)

(defn init []
  "Constructs the current development system."
  (alter-var-root #'system
    (constantly (app/system))))

(defn start []
  "Starts the current development system."
  (alter-var-root #'system component/start))

(defn stop []
  "Shuts down and destroys the current development system."
  (alter-var-root #'system
    (fn [s] (when s (component/stop s)))))

(defn go []
  "Initializes the current development system and starts it running."
  (init)
  (start))

(defn reset []
  "Reinitializes and restarts the entire development system."
  (stop)
  (refresh :after 'user/go))
