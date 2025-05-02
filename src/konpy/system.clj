(ns konpy.system
  (:require [ring.adapter.jetty :as jetty]
            [taoensso.telemere :as t]
            [konpy.routes :as routes]
            [konpy.db :as db]))

(def storage "storage/db.sqlite")

(defn start-db
  []
  (db/start storage))

(defn stop-db
  []
  (db/stop))

(defn start-server
  []
  (let [server (jetty/run-jetty
                #'routes/root-handler
                {:port  3000, :join? false})]
    (t/log! :info "server started at port 3000.")
    server))

(defn stop-server
  [server]
  (.stop server)
  (t/log! :info "server stopped."))

(defn start-system
  []
  (start-db)
  {::server (start-server)})

(defn stop-system
  [system]
  (stop-db)
  (stop-server (::server system)))
