(ns konpy.system
  (:require [ring.adapter.jetty :as jetty]
            ; [clj-reload.core :as reload]
            [pg.core :as pg]
            [taoensso.telemere :as t]
            [environ.core :refer [env]]
            [konpy.routes :as routes]
            [konpy.db :as db]))

(defn start-db
  []
  (db/start "storage/db.sqlite"))

(defn stop-db
  []
  (db/stop))

(def pg-config
  {:host "127.0.0.1"
   :user (env :pg-user)
   :password (env :pg-pass)
   :database "typing_ex"})

(def pg-conn pg-config)

(defonce server (atom nil))

(defn start-server
  []
  (let [port (or (env :port) "3000")
        handler (if (= (env :develop) "true")
                  #'routes/root-handler
                  routes/root-handler)]
    (reset! server
            (jetty/run-jetty
             handler
             {:port (parse-long port) :join? false}))
    (t/log! :info (str "server started at port " port))
    #_(alter-var-root #'pg-conn (pg/connect pg-config))))

(defn stop-server
  []
  (when @server
    (.stop @server)
    (t/log! :info "server stopped.")
    (pg/close pg-conn)))

(defn start-system
  []
  (start-db)
  (start-server))

(defn stop-system
  []
  (stop-db)
  (stop-server))

(defn restart-system
  []
  (stop-system)
  (start-system))
