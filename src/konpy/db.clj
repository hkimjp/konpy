(ns konpy.db
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [datascript.core :as d]
   [datascript.storage.sql.core :as storage-sql]
   [taoensso.telemere :as t]))

(defonce storage (atom nil))

(def conn nil)

(defn conn? []
  (d/conn? conn))

(defn- make-storage [db]
  (t/log! :info (str "make-stroage " db))
  (try
    (let [datasource (doto (org.sqlite.SQLiteDataSource.)
                       (.setUrl (str "jdbc:sqlite:" db)))
          pooled-datasource (storage-sql/pool
                             datasource
                             {:max-conn 10
                              :max-idle-conn 4})]
      (storage-sql/make pooled-datasource {:dbtype :sqlite}))
    (catch Exception e
      (t/log! :error (.getMessage e))
      (throw (Exception. "db dir does not exist.")))))

(def schema nil)

(defn- create
  ([]
   (t/log! :info "create on-memory datascript.")
   (d/create-conn schema))
  ([db]
   (reset! storage (make-storage db))
   (t/log! :info "create sqlite backended datascript.")
   (d/create-conn schema {:storage @storage})))

(defn- restore
  [db]
  (t/log! {:level :info :data db} "restore")
  (reset! storage (make-storage db))
  (t/log! "reset storage")
  (d/restore-conn @storage) ; <- tagged literal issue.
  )

(defn gc []
  (t/log! :info "db/gc")
  (d/collect-garbage @storage))

(comment
  (gc)
  :rcf)

(defn start
  ([]
   (t/log! :info "start on-memory datascript.")
   (alter-var-root #'conn (constantly (create))))
  ([db]
   (t/log! :info "start datascript with sqlite backend.")
   (if (.exists (io/file db))
     (alter-var-root #'conn (constantly (restore db)))
     (alter-var-root #'conn (constantly (create db))))))

(defn stop []
  (t/log! :info "db stopped")
  (storage-sql/close @storage)
  (reset! storage nil)
  (alter-var-root #'conn (constantly nil)))

(comment
  (start "data/db.sqlite")

  (d/pull @conn '[*] 7745)
  (d/q '[:find ?week ?num
         :in $ ?eid
         :where
         [?e :week ?week]
         [?e :num ?num]
         [(= ?e ?eid)]]
       @conn 7745)

  (q '[:find ?week ?num
       :keys week num
       :in $ ?eid
       :where
       [?e :week ?week]
       [?e :num ?num]
       [(= ?e ?eid)]]
     7745)

  (d/entity @conn 7745)
  :rcf)
;------------------------------------------

(defn- shorten
  ([s] (shorten s 80))
  ([s n] (let [pat (re-pattern (str "(^.{" n "}).*"))]
           (str/replace-first s pat "$1..."))))

(defn put! [facts]
  (t/log! :info (str "put! " (shorten facts)))
  (d/transact! conn facts))

(defmacro q [query & inputs]
  (t/log! :info (str "q " query))
  `(d/q ~query @conn ~@inputs))

(defn pull
  ([eid] (pull '[*] eid))
  ([selector eid]
   (t/log! :info (str "pull " selector " " eid))
   (d/pull @conn selector eid)))

(defn entity
  [eid]
  (t/log! :info (str "entity " eid))
  (d/entity @conn eid))

;------------------------------------------
