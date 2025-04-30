(ns konpy.example
  (:require [konpy.views :refer [page]]))

(defn example-page [_request]
  (page
   [:div
    [:p {:class "text-4xl bg-red-500 font-medium text-white"}
     "Example"]
    [:button {:class "bg-yellow-500 hover:bg-yellow-100"}
     "example page. need reload? or?"]
    [:p "Please login. are you there?"]
    [:form {:method "post"}
     [:input {:placeholder "your account" :name "login"}]
     [:input {:type "password" :name "password"}]
     [:button
      {:class "bg-sky-100 hover:bg-sky-300 active:bg-red-500"}
      "LOGIN"]]]))

(defn example-post [request]
  (page
   [:div
    [:p "body: " (slurp (:body request))]]))
