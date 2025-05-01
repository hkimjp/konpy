(ns konpy.db
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [datascript.core :as d]
   [datascript.storage.sql.core :as storage-sql]
   [taoensso.telemere :as t]))

(defonce storage (atom nil))

; (def conn nil)

(defn conn? [conn]
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

(defn- create
  ([]
   (t/log! :info "create on-memory datascript.")
   (d/create-conn nil))
  ([db]
   (reset! storage (make-storage db))
   (t/log! :info "create sqlite backended datascript.")
   (d/create-conn nil {:storage @storage})))

(defn- restore
  [db]
  (t/log! {:level :info :data db} "restore")
  (reset! storage (make-storage db))
  (d/restore-conn @storage))

(defn gc []
  (d/collect-garbage @storage))

(defn start
  ([]
   (t/log! :info "start on-memory datascript.")
   (create))
  ([db]
   (t/log! :info "start datascript with sqlite backend.")
   (if (.exists (io/file db))
     (restore db)
     (create db))))

(defn stop []
  (t/log! :info "db stopped")
  (storage-sql/close @storage))

;------------------------------------------

(defn put [conn fact]
  (t/log! :info (str "put " fact))
  (d/transact! conn [fact]))

(defn puts [conn facts]
  (t/log! :info (str "put " facts))
  (d/transact! conn facts))

(defmacro q [conn query & inputs]
  (t/log! :info (str "q " query))
  `(d/q ~query @~conn ~@inputs))

(defn pull
  ([conn eid] (pull conn '[*] eid))
  ([conn selector eid]
   (t/log! :info (str "pull " selector " " eid))
   (d/pull @conn selector eid)))


