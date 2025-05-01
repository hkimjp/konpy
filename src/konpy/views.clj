(ns konpy.views
  (:require [hiccup2.core :as h]
            [ring.util.response :as response]))

(defn base
  [content]
  [:html {:lang "en"}
   [:head
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1"}]
    [:link {:type "text/css"
            :rel "stylesheet"
            :href "/css/output.css"}]
    [:title "今週のPython"]]
   [:body
    [:div {:class "container"}
     content
     [:script {:type "text/javascript"
               :src "/js/htmx.min.js"
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

(defn under-construction
  [_request]
  (page [:div {:class "text-4xl bg-red-500"} "UNDER CONSTRUCTION"]))

(comment
  (under-construction nil)
  :rcf)
