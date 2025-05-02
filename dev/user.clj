(ns user
  (:require [konpy.system :as system]
            [taoensso.telemere :as t]
            [konpy.db :as db]
            [environ.core :refer [env]]
            konpy.core-test
            #_[clj-reload.core :as reload]))

(env :develop)
(env :port)
(system/start-system)
(comment
  (db/conn?)
  (db/start "storage/db.sqlite")
  (db/conn?)
  (db/put [{:db/id -1 :name "hiroshi" :sex "male" :age 63}])

  (db/put [[:db/add -1 :name "akari"]
           [:db/add -1 :work "kyoto"]
           [:db/add -1 :age 32]])

  (def eid '[:find ?e
             :where
             [?e]])

  (db/q eid)

  (db/pull 1)

  ;; how to use `db/entity`?
  (db/entity 1)

  (def name-age-q '[:find ?name ?age
                    :where
                    [?e :name ?name]
                    [?e :age ?age]])

  (db/q name-age-q)

  (db/q '[:find ?e ?name ?age
          :in $ ?name
          :where
          [?e :name ?name]
          [?e :age ?age]]
        "akari")

  (db/pull ['*] 1)
  (db/pull  [:work] 1)

  (db/pull [:name :age] 1)

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
