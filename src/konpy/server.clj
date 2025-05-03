(ns konpy.server
  (:require
   [ring.adapter.jetty :as jetty]
   #_[ring.util.response :as response]
   [taoensso.telemere :as t]
   [konpy.routes :refer [root-handler]]))

(def server (atom nil))

(defn start-server []
  (reset! server (jetty/run-jetty
                #'root-handler
                {:port  3000, :join? false}))
   (t/log! :info "server started at port 3000.")
   server)

(defn stop-server []
  (.stop @server)
  (t/log! :info "server stopped."))

(comment
  (start-server)
  (stop-server)
  :rcf)
