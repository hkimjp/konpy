(ns konpy.assignments
  (:require [taoensso.telemere :as t]
            [konpy.views :refer [page]]
            [hiccup2.core :as h]
            [ring.util.response :as resp]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [environ.core :refer [env]]))

(defn under-construction [_]
  (page
   [:div {:class ""} "under construction"]))

(defn login [request]
  (get-in request [:session :identity]))

(defn list [request]
  (page
   [:div
    [:div {:class ""} "list"]
    [:div "user:" (login request)]]))

(defn list-all [_]
  (under-construction nil))

(defn all [_]
  (under-construction nil))

(defn edit [_]
  (under-construction nil))

(defn edit! [_]
  (under-construction nil))

(defn delete! [_]
  (under-construction nil))

(defn new [_]
  (under-construction nil))

(defn create! [_]
  (under-construction nil))

