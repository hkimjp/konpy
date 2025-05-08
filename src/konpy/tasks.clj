(ns konpy.tasks
  (:require
   [konpy.utils :refer [user weeks admin?]]
   [konpy.views :refer [page]]
   [konpy.db :refer [q]]))

(def box-sky  "rounded-xl text-white p-1 bg-sky-500 hover:bg-sky-700 active:bg-red-500")

(def box-lime "my-1 p-1 rounded-xl text-white bg-lime-500 hover:bg-lime-700 active:bg-red-500")

(def box-red "rounded-xl text-white p-1 bg-red-500 hover:bg-red-700 active:bg-red-500")

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
     [:div.mx-4
      (for [{:keys [e week num task]} ret]
        [:div {:class "flex my-2"}
         [:span (str week "-" num " " task)]
         [:span
          [:a {:class box-sky
               :href (str "/answer/" e)}
           "回答"]]])
      [:button {:class box-lime
                :hx-get "/answers/recent/10"
                :hx-target "#answers"
                :hx-swap "outerHTML"}
       "最近の回答者"]
      [:div#answers]
      [:button {:class box-lime
                :hx-get "/answers/logins/10"
                :hx-target "#logins"
                :hx-swap "outerHTML"}
       "最近のログイン"]
      [:div#logins]
      (when (admin? (user request))
        [:div {:class "py-2"}
         [:a {:class box-red
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
     [:div {:class "mx-4 py-2"}
      (for [{:keys [week num task]} ret]
        [:p (str week "-" num " " task)])])))
