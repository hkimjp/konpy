(ns konpy.answers
  (:require
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [konpy.utils :refer [user]]
   [konpy.views :refer [page under-construction-page]]))

(defn answer [request]
  (page
    [:div
     [:div "name: " (user request)]
     [:div "Q:" ]
     [:div
      [:form {:method "post"}
       (h/raw (anti-forgery-field))
       [:input {:type "hidden" :name "login" :value (user request)}]
       [:div [:textarea {:class "s-300"
                         :name "answer"} "your answer"]]
       [:div [:button {:class "bg-red"} "submit"]]]]]))

(defn answer! [{params :params}]
  (let [params (dissoc params :__anti-forgery-token)]
    (page
      [:div (str params)])))
