(ns konpy.system
  (:require [ring.adapter.jetty :as jetty]
            [konpy.routes :as routes]
            [taoensso.telemere :as t]))

(defn start-db
  [])

(defn stop-db
  [db])

(defn start-server []
  (t/log! :info "server started at port 3000.")
  (let [server (jetty/run-jetty
                #'routes/root-handler
                {:port  3000, :join? false})]
    server))

(defn stop-server [server]
  (.stop server)
  (t/log! :info "server stopped."))

(defn start-system
  []
  {::server (start-server)})

(defn stop-system
  [system]
  (stop-server (::server system)))
