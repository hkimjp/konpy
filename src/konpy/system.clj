(ns konpy.system
  (:require [ring.adapter.jetty :as jetty]
            [taoensso.telemere :as t]
            [environ.core :refer [env]]
            [konpy.carmine :refer [ping?]]
            [konpy.routes :as routes]
            [konpy.db :as db]))

(defn start-datascript
  []
  (db/start "storage/db.sqlite"))

(defn stop-datascript
  []
  (db/stop))

(defonce server (atom nil))

(defn start-jetty
  []
  (let [port (or (env :port) "3000")
        handler (if (= (env :develop) "true")
                  #'routes/root-handler
                  routes/root-handler)]
    (reset! server
            (jetty/run-jetty
             handler
             {:port (parse-long port) :join? false}))
    (t/log! :info (str "server started at port " port))))

(defn stop-server
  []
  (when @server
    (.stop @server)
    (t/log! :info "server stopped.")))

(defn start-system
  []
  (if (ping?)
    (do
      (start-datascript)
      (start-jetty))
    (println "can not talk to redis.")))

(defn stop-system
  []
  (stop-datascript)
  (stop-server))

(defn restart-system
  []
  (stop-system)
  (start-system))
