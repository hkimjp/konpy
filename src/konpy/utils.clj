(ns konpy.utils
  (:require
   [environ.core :refer [env]]
   [java-time.api :as jt]))

; FIXME: tagged literal?
(comment
  (jt/local-date)
  (jt/instant)
  (java.util.Date.)
  :rcf)

(defn develop?
  []
  (= (env :develop) "true"))

(defn user [request]
  (get-in request [:session :identity]))

(def start-day (jt/local-date 2025 4 2))

(defn weeks
  "Returns how many weeks have passed since the argument `date`.
   If no argument given, use the `start`day` defnied above."
  ([] (weeks (jt/local-date)))
  ([date]
   (quot (jt/time-between start-day date :days) 7)))

(comment
  (= 4 (weeks (jt/local-date 2025 5 6)))
  (= 5 (weeks (jt/local-date 2025 5 7)))
  :rcf)
