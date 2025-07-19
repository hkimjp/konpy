(ns konpy.views
  (:require [hiccup2.core :as h]
            [ring.util.response :as response]))

(def ^:private version "0.28.4")

(defn base
  [content]
  [:html {:lang "en"}
   [:head
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1"}]
    [:link {:type "text/css"
            :rel  "stylesheet"
            :href "/assets/css/output.css"}]
    [:title "kp"]]
   [:body {:hx-boost "true"}
    [:div {:class "mx-auto"}
     [:div {:class "font-meduim text-4xl text-white bg-sky-700"}
      [:a {:href "/tasks"} "今週の Python"]]
     content
     [:hr]
     "hkimura "
     version
     [:script {:type "text/javascript"
               :src  "/assets/js/htmx.min.js"
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
  (-> (str (h/html (h/raw "<!DOCTYPE html>") (base content)))
      response/response
      (response/header "Content-Type" "text/html")))

(defn under-construction-page [_]
  (page
   [:div {:class "text-2xl text-red-500"} "Sorry, Under Construction."]))

(defn yet [_]
  (render [:div "再読み込みで戻るはず。"]))
