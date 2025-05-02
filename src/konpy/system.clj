(ns konpy.system
  (:require [ring.adapter.jetty :as jetty]
            [taoensso.telemere :as t]
            [konpy.routes :as routes]
            [konpy.db :as db]
            [environ.core :refer [env]]))

(defn start-db
  []
  (db/start "storage/db.sqlite"))

(defn stop-db
  []
  (db/stop))

(def server (atom nil))

(defn start-server
  []
  (let [port (or (env :port) "3000")]
    (reset! server
            (jetty/run-jetty
             #'routes/root-handler
             {:port (parse-long port) :join? false}))
    (t/log! :info (str "server started at port " port))))

(defn stop-server
  []
  (.stop @server)
  (t/log! :info "server stopped."))

(defn start-system
  []
  (start-db)
  (start-ser

(defn stop-system
  [system]
  (stop-db)
  (stop-server (::server system)))

