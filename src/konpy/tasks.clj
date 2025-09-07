(ns konpy.tasks
  (:require
   ; [konpy.carmine :as c]
   [konpy.db :refer [q]]
   [konpy.utils :refer [user weeks admin?]]
   [konpy.views :refer [page]]))

(def box-sky  "px-1 rounded-xl text-white bg-sky-500 hover:bg-sky-700 active:bg-red-500")

(def box-lime "my-2 px-1 rounded-xl text-white bg-lime-500 hover:bg-lime-700 active:bg-red-500")

(def box-red "rounded-xl text-white p-1 bg-red-500 hover:bg-red-700 active:bg-red-500")

(defn tasks-this-week
  "show this weeks assignments.
   this page must provide link to answer and views."
  [request]
  (let
   ; 学期終了のため、全問題、回答を晒す。
   ; [tasks-q '[:find ?e ?week ?num ?task
   ;                :keys e week num task
   ;                :in $ week
   ;                :where
   ;                [?e :week ?week]
   ;                [?e :num ?num]
   ;                [?e :task ?task]]
   ;      ret (->> (q tasks-q (week))
   ;               (sort-by (juxt :week :num)))]
   [tasks-q '[:find ?e ?week ?num ?task
              :keys e week num task
              :in $
              :where
              [?e :week ?week]
              [?e :num ?num]
              [?e :task ?task]]
    ret (->> (q tasks-q)
             (sort-by (juxt :week :num)))]
    (page
     [:div.mx-4
      [:p {:class "m-2 text-xl text-red-500"}
       "関数を回答するときは関数コメント、doctest を忘れずに。"]
      (for [{:keys [e week num task]} ret]
        [:div {:class "flex my-2 gap-x-2"}
         [:span (str week "-" num ", ")]
         [:div {:class "flex items-end"}
          [:pre {:class ""} task]
          [:a {:class box-sky
               :href (str "/answer/" e)}
           "回答"]]])
      [:button {:class box-lime
                :hx-get "/answers/answers"
                :hx-target "#answers"
                :hx-swap "outerHTML"}
       "回答(12h)"]
      [:div#answers]
      [:button {:class box-lime
                :hx-get "/answers/logins"
                :hx-target "#logins"
                :hx-swap "outerHTML"}
       "ログイン(24h)"]
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
