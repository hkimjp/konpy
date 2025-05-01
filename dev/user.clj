(ns user
  (:require [konpy.system :as system]
            [taoensso.telemere :as t]
            [konpy.db :as db]
            #_[clj-reload.core :as reload]))

(comment
  (def conn (db/start
             "storage/db.sqlite"))
  (db/conn? conn)

  (db/put conn '{:db/id -1 :name "kimura" :sex "male" :age 63})

  (db/q conn '[:find ?e ?name ?age
               :where
               [?e :name ?name]
               [?e :age ?age]])

  (db/pull conn '[*] 1)
  (db/pull conn 1)
  (db/pull conn [:name :age] 8)
  (db/q conn '[:find (count ?e)
               :where
               [?e _ _]])
  (db/stop)
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
