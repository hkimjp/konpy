(ns user
  (:require [konpy.system :as system]
            [taoensso.telemere :as t]
            [clj-reload.core :as reload]))

(def system nil)

(defn start-system!
  []
  (if system
    (t/log! :info "Already Started")
    (alter-var-root #'system (constantly (system/start-system)))))

(defn stop-system!
  []
  (when system
    (system/stop-system system)
    (alter-var-root #'system (constantly nil))))

(defn restart-system!
  []
  (stop-system!)
  (start-system!))

(defn server
  []
  (::system/server system))

(comment
  (restart-system!)
  :rcf)
