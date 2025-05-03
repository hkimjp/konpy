(ns user
  (:require [konpy.system :as system]
            [taoensso.telemere :as t]
            [konpy.db :as db]
            [environ.core :refer [env]]
            konpy.core-test))

(t/set-min-level! :debug)

(system/start-system)

(comment
  (env :port)

  (reload/reload)

  (system/start-system)

  (system/restart-system)

  (db/conn?)
  (db/start "storage/db.sqlite")
  (db/conn?)

  (def tasks-q '[:find ?e ?num ?week ?task ?deadline ?issued
                 :in $ ?week
                 :where
                 [?e :num ?num]
                 [?e :week ?week]
                 [?e :task ?task]
                 [?e :deadline ?deadline]
                 [?e :issued ?issued]])

  (db/q tasks-q "1")

  (db/put! [{:db/id -1 :name "hiroshi" :sex "male" :age 63}])

  (db/put! [[:db/add -1 :name "isana"]
            [:db/add -1 :work "police"]
            [:db/add -1 :age 28]])

  (def eid '[:find ?e
             :where
             [?e]])

  (db/q eid)

  (db/pull 3)

  ;; how to use `db/entity`?
  (db/entity 3)

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

(comment
  (restart-system!)
  (server)
  :rcf)
