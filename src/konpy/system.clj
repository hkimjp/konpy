(ns konpy.system
  (:require [ring.adapter.jetty :as jetty]
            [konpy.routes :as routes]
            [taoensso.telemere :as t]))

(defn start-server []
  (let [server (jetty/run-jetty
                #'routes/root-handler
                {:port  3000, :join? false})]
    (t/log! :info "server started at port 3000.")
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
