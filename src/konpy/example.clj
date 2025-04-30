(ns konpy.example
  (:require [konpy.views :refer [page]]))

(defn example-handler [_request]
  (page
   [:div {:class "mx-auto"}
    [:div {:class "mx-auto items-center"}
     [:p {:class "text-4xl bg-red-500 font-medium text-white"}
      "Example"]
     [:p {:class "bg-yellow-500"} "example page. need reload? or?"]]]))
