(ns konpy.views
  (:require [hiccup2.core :as h]
            [ring.util.response :as response]))

(def ^:private version "0.4.5-SNAPSHOT")

(defn base
  [content]
  [:html {:lang "en"}
   [:head
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1"}]
    [:link {:type "text/css"
            :rel "stylesheet"
            :href "/assets/css/output.css"}]
    [:title "kp"]]
   [:body#body
    [:div {:class "mx-auto"}
     [:div {:class "font-meduim text-4xl text-white bg-sky-700"} "今週のPython"]
     content
     [:hr]
     "hkimura "
     version
     [:script {:type "text/javascript"
               :src "/assets/js/htmx.min.js"
               :defer true}]]]])

(defn render
  [content]
  (-> content
      h/html
      str
      (response/response)
      (response/header "Content-Type" "text/html")))

(defn page
  [content]
  (-> content
      base
      render))
