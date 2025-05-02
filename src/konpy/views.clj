(ns konpy.views
  (:require [hiccup2.core :as h]
            [ring.util.response :as response]))

(def ^:private version "0.4.1")

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
    [:title "今週のPython"]]
   [:body
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

(defn under-construction
  [request]
  (page
   [:div {:class "mx-auto items-center"}
    [:div {:class "text-4xl bg-red-500 text-white"} "UNDER CONSTRUCTION"]
    [:div {:class "font-medium text-sky-500"}
     [:p  "uri: " (:uri request)]]]))

(comment
  (:body (under-construction {:uri "hellow"}))
  :rcf)
