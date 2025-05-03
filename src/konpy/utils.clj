(ns konpy.utils
  (:require [konpy.views :refer [page]]))

(defn now []
  (java.util.Date.))

(defn under-construction [_]
  (page
   [:div {:class ""} "under construction"]))

(defn user [request]
  (get-in request [:session :identity]))
