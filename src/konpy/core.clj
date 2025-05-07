(ns konpy.core
  (:require [konpy.system :as system])
  (:gen-class))

(defn -main
  [& _args]
  (system/start-system))
