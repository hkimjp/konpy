(ns konpy.system
  (:require [ring.adapter.jetty :as jetty]
            ; [clj-reload.core :as reload]
            [taoensso.telemere :as t]
            [environ.core :refer [env]]
            [konpy.routes :as routes]
            [konpy.db :as db]))

; (set! *default-data-reader-fn* clojure.core/tagged-literal)
; (alter-var-root #'*default-data-reader-fn* (constantly tagged-literal))
; *default-data-reader-fn*

(defn start-db
  []
  (db/start "storage/db.sqlite"))

(defn stop-db
  []
  (db/stop))

(defonce server (atom nil))

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
  (when @server
    (.stop @server)
    (t/log! :info "server stopped.")))

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
  ; (reload/reload)
  (start-system))

(comment
  (start-server)
  (start-db)
  (stop-server)
  (stop-db)
  (start-system)
  (stop-system)
  (restart-system)
  :rcf)
