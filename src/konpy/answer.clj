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
    [:div "Q:"]
    [:div
     [:label "answer:"]
     [:form {:method "post"}
      (h/raw (anti-forgery-field))
      [:div [:textarea {:class "w-120 h-60 outline outline-black/5 shadow-lg"
                        :name "answer"
                        :prompt "your answer"}]]
      [:div [:button
             {:class "bg-sky-100 hover:bg-sky-500 active:bg-red-500 rounded-2xl"}
             "送信"]]]]]))

(defn put-answer! [{:keys [eid author answer]}]
  (t/log! :info (str "put-answer " eid author answer))
  (db/put! [{:db/add -1 :author author :answer answer :to eid :identical []}]))

(defn answer! [{params :params :as request}]
  (let [params (dissoc params :__anti-forgery-token)]
    (put-answer!
     (merge params (:path-params request) {:author (user request)}))
    (page
     [:div
      [:div "user " (user request)]
      [:div "params " (str params)]
      [:div "path-params " (str (:path-params request))]])))
