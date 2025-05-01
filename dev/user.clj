(ns user
  (:require [konpy.system :as system]
            [taoensso.telemere :as t]
            [konpy.db :as db]
            #_[clj-reload.core :as reload]))

(comment
  (def conn (db/start "storage/db.sqlite"))

  (db/conn?)

  (db/put '{:db/id -1 :name "miyuki" :sex "female" :age 57})

  (db/q '[:find ?e ?name ?age
          :in $ ?name
          :where
          [?e :name ?name]
          [?e :age ?age]]
        "miyuki")

  (db/pull '[*] 1)
  (db/pull  1)
  (db/pull [:name :age] 8)
  (db/q '[:find (count ?e)
          :where
          [?e _ _]])
  (db/stop)
  (db/conn?)

  :rcf)

(def system nil)

(defn start-system!
  []
  (if system
    (t/log! :info "Already Started")
    (alter-var-root #'system (constantly (system/start-system)))))

(defn stop-system!
  []
  (when system
    (system/stop-system system)
    (alter-var-root #'system (constantly nil))))

(defn restart-system!
  []
  (stop-system!)
  (start-system!))

(defn server
  []
  (::system/server system))

(comment
  (restart-system!)
  (server)
  :rcf)
