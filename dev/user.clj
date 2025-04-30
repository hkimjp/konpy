(ns user
  (:require [konpy.server :as s]
            [clj-reload.core :as reload]))

(def server (atom nil))

(defn restart!
  []
  (if (some? @server)
    (do
      (s/stop-server @server)
      (reload/reload)
      (reset! server (s/start-server nil)))
    (do
      (reset! server (s/start-server nil)))))

(comment
  (restart!)
  :rcf)
