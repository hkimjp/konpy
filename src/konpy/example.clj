(ns konpy.example
  (:require [konpy.views :refer [page render]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [hiccup2.core :as h]
            [taoensso.telemere :as t]))

(defn example-page [_request]
  (page
   [:div
    [:p {:class "text-4xl bg-red-500 font-medium text-white"}
     "Example"]
    [:button {:class "bg-yellow-500 hover:bg-yellow-100"}
     "example page. need reload? or?"]
    [:p "Please login. are you there?"]
    [:form {:method "post"}
     (h/raw (anti-forgery-field))
     [:input {:placeholder "your account" :name "login"}]
     [:input {:type "password" :name "password"}]
     [:button
      {:class "bg-sky-100 hover:bg-sky-300 active:bg-red-500"}
      "LOGIN"]]
    [:button
     {:class "bg-sky-500 active:bg-red-500"
      :hx-confirm "are you sure?"
      :hx-get     "/example/confirm"
      :hx-target  "#confirm"
      :hx-swap    "outerHTML"}
     "confirm"]
    [:div#confirm "not yet confirmed"]]))

(defn example-post [{{:keys [login password]} :params}]
  (t/log! :info "called example-post")
  (page
   [:div
    [:p "login: " login]
    [:p "password: " password]]))

(defn example-confirm [_]
  (t/log! :info "called example-confirm")
  (render [:div#confirm "confirmed"]))

(comment
  (example-confirm nil)
  :rcf)
