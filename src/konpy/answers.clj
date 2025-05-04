(ns konpy.answers
  (:require
   [konpy.views :refer [page under-construction-page]]))

(defn answer [_request]
  (under-construction-page nil))

(defn answer! [_request]
  (under-construction-page nil))
