(ns konpy.answer
  (:require
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [taoensso.telemere :as t]
   [konpy.db :as db]
   [konpy.utils :refer [user]]
   [konpy.views :refer [page]]))

(defn answer [request]
  (t/log! {:level :info
           :data {:params (:params request)
                  :path-params (:path-params request)}}
    "answer")
  (page
    [:div
     [:div "name: " (user request)]
     [:div "Q:" ]
     [:div
      [:label "answer:"]
      [:form {:method "post"}
       (h/raw (anti-forgery-field))
       [:div [:textarea {:class "w-120 outline outline-black/5 shadow-lg"
                         :name "answer"
                         :prompt "your answer"}]]
       [:div [:button {:class "bg-red-500"} "submit"]]]]]))

(defn put-answer! [{:keys [eid author answer]}]
  (t/log! :info (str eid author answer)))

(defn answer! [{params :params :as request}]
  (let [params (dissoc params :__anti-forgery-token)]
    (put-answer!
      (merge params (:path-params request) {:author (user request)}))
    (page
      [:div
       [:div "user " (user request)]
       [:div "params " (str params)]
       [:div "path-params " (str (:path-params request))]])))
