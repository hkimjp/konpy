(ns konpy.core
  (:require [konpy.system :as system])
  (:gen-class))

; (set! *default-data-reader-fn* tagged-literal)

(defn -main
  [& _args]
  (system/start-system))

