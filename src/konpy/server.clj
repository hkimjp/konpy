(ns konpy.server
  (:require
   [ring.adapter.jetty :as jetty]
   [ring.util.response :as response]
   [taoensso.telemere :as t]
   [konpy.routes :refer [root-handler]]))

(defn start-server [_system]
  (let [server (jetty/run-jetty
                #'root-handler
                {:port  3000, :join? false})]
    (t/log! :info "server started at port 3000.")
    server))

(defn stop-server [server]
  (.stop server)
  (t/log! :info "server stopped."))

