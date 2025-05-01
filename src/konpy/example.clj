(ns konpy.example
  (:require [konpy.views :refer [page]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [hiccup2.core :as h]))

(defn example-page [_request]
  (page
   [:div
    [:p {:class "text-4xl bg-red-500 font-medium text-white"}
     "Example"]
    [:button {:class "bg-yellow-500 hover:bg-yellow-100"}
     "example page. need reload? or?"]
    [:p "Please login. are you there?"]
    [:form {:method "post"}
     (h/raw (ring.util.anti-forgery/anti-forgery-field))
     [:input {:placeholder "your account" :name "login"}]
     [:input {:type "password" :name "password"}]
     [:button
      {:class "bg-sky-100 hover:bg-sky-300 active:bg-red-500"}
      "LOGIN"]]]))

(defn example-post [request]
  (page
   [:div
    [:p "body: " (slurp (:body request))]
    [:p ":params" (str (:params request))]]))

