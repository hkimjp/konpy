(ns konpy.tasks
  (:require
   [konpy.utils :refer [user weeks admin?]]
   [konpy.views :refer [page]]
   [konpy.db :refer [q]]))

(defn tasks-this-week
  "show this weeks assignments.
   this page must provide link to answer and views."
  [request]
  (let [tasks-q '[:find ?e ?week ?num ?task
                  :keys e week num task
                  :in $ ?week
                  :where
                  [?e :week ?week]
                  [?e :num ?num]
                  [?e :task ?task]]
        ret (->> (q tasks-q (weeks))
                 (sort-by :num))]
    (page
     [:div
      (for [{:keys [e week num task]} ret]
        [:div {:class "flex"}
         [:span (str week "-" num " " task)]
         [:span
          [:a {:class "rounded-xl text-white bg-sky-500 hover:bg-sky-700 active:bg-red-500"
               :href (str "/answer/" e)}
           "回答"]]])
      (when (admin? (user request))
        [:div [:a {:class "rounded-xl text-white bg-red-500 hover:bg-red-700 active:bg-red-500"
                   :href (str "/admin")}
               "admin"]])])))

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
      (for [{:keys [week num task]} ret]
        [:p (str week "-" num " " task)])])))
