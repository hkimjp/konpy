(ns konpy.assignments
  (:require [taoensso.telemere :as t]

            [hiccup2.core :as h]
            [ring.util.response :as resp]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            ; [environ.core :refer [env]]
            [konpy.utils :refer [user now]]
            [konpy.views :refer [page]]
            [konpy.db :refer [put! q pull entity]]))

(defn task
  "show this weeks assignments.
   this page must provide link to answer and views."
  [request]
  (page
   [:div
    [:div {:class ""} "list"]
    [:div "user:" (user request)]]))

(defn tasks
  "list all the tasks"
  [_]
  (let [tasks-q '[:find ?week ?num ?task ?deadline
                  :keys week num task deadline
                  :where
                  [?e :week ?week]
                  [?e :num ?num]
                  [?e :task ?task]
                  [?e :deadline ?deadline]]
        ret (q tasks-q)]
    (page
     [:div
      [:div
       (for [{:keys [week num task deadline]} ret]
         [:p (str week "-" num " " deadline " " task)])]])))

; (pull 8)
