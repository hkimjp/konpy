(ns konpy.core
  (:require [konpy.server :as s])
  (:gen-class))

(defn -main
  [& _args]
  (s/start-server nil))

