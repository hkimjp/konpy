(ns konpy.utils
  (:require
   [java-time.api :as jt]
   [konpy.views :refer [page render]]))

; views namespace?
(defn under-construction-page [_]
  (page
   [:div {:class ""} "under construction"]))

(defn yet [_]
  (render [:div "再読み込みで戻るはず。"]))

; FIXME: tagged literal?
(defn now []
  (str (jt/local-date))
  #_(java.util.Date.)
  #_(jt/local-date))

(comment
  (now)
  (jt/local-date)
  (jt/instant)
  (java.util.Date.)
  :rcf)

(defn user [request]
  (get-in request [:session :identity]))

(def start-day (jt/local-date 2025 3 31))

(defn weeks
  "Returns how many weeks have passed since the argument `date`.
   If no argument given, use the `start`day` defnied above."
  ([] (weeks (jt/local-date)))
  ([date]
   (quot (jt/time-between start-day date :days) 7)))

(comment
  (weeks (jt/local-date 2025 6 6))
  :rcf)
