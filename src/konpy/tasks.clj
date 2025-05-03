(ns konpy.tasks
  (:require [taoensso.telemere :as t]
            [hiccup2.core :as h]
            [ring.util.response :as resp]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            ; [environ.core :refer [env]]
            [konpy.utils :refer [user now weeks under-construction-page]]
            [konpy.views :refer [page]]
            [konpy.db :refer [put! q]]))

(defn tasks-this-week
  "show this weeks assignments.
   this page must provide link to answer and views."
  [_]
  (let [tasks-q '[:find ?week ?num ?task
                  :keys week num task
                  :in $ ?week
                  :where
                  [?e :week ?week]
                  [?e :num ?num]
                  [?e :task ?task]]
        ret (->> (q tasks-q (weeks))
                 (sort-by :num))]
    (page
     [:div
      [:div
       (for [{:keys [week num task]} ret]
         [:div {:class "flex"}
          [:span (str week "-" num " " task)]
          [:button
           {:class "rounded-xl text-white bg-sky-500 hover:bg-sky-700 active:bg-red-500"
            :hx-confirm "まだ未完成です。"
            :hx-get "/tasks/yet"
            :hx-swap "outerHTML"}
           "回答"]])]])))

(defn tasks-all
  "no edit."
  [_]
  (let [tasks-q '[:find ?week ?num ?task
                  :keys week num task
                  :where
                  [?e :week ?week]
                  [?e :num ?num]
                  [?e :task ?task]]
        ret (->> (q tasks-q)
                 (sort-by (juxt :week :num)))]
    (page
     [:div
      [:div
       (for [{:keys [week num task]} ret]
         [:p (str week "-" num " " task)])]])))
