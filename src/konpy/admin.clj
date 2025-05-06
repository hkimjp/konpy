(ns konpy.admin
  (:require [taoensso.telemere :as t]
            [hiccup2.core :as h]
            [ring.util.response :as resp]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
   ; [environ.core :refer [env]]
            [konpy.db :refer [put! q]]
            [konpy.utils :refer [now]]
            [konpy.views :refer [page  under-construction-page]]))

(defn tasks
  "list all the tasks. edit, delete and add."
  [_]
  (let [tasks-q '[:find ?e ?week ?num ?task ?issued
                  :keys e week num task issued
                  :where
                  [?e :week ?week]
                  [?e :num ?num]
                  [?e :task ?task]
                  [?e :issued ?issued]]
        ret (->> (q tasks-q)
                 (sort-by (juxt :week :num)))]
    (page
     [:div
      [:div [:a {:href "/tasks"
                 :class "rounded-xl text-white bg-red-500 hover:bg-red-700 active:bg-red-500"}
             "tasks"]]
      (for [{:keys [e week num task]}
            (conj ret {:e -1 :week "" :num "" :task ""})]
        [:div {:class "flex"}
         [:form {:class "mx-xl" :method "post"}
          (h/raw (anti-forgery-field))
          [:div {:class "flex items-center"}
           [:input {:type "hidden" :name "e" :value e}]
           [:input {:class "text-center size-10 shadow-lg outline outline-black/5"
                    :name "week"
                    :value week}]
           " - "
           [:input {:class "text-center size-10 shadow-lg outline outline-black/5"
                    :name "num"
                    :value num}]
           [:textarea {:class "w-120 outline outline-black/5 shadow-lg"
                       :name "task"}
            task]
           [:button {:class "rounded-xl text-white bg-sky-500 hover:bg-sky-700 active:bg-red-500"}
            "update"]]]])
      [:div {:class "flex"}
       [:p [:a {:href "/admin/new"
                :class "rounded-xl text-white bg-sky-500 hover:bg-sky-700 active:bg-red-500"}
            "new"]]]])))

; use?
(defn new [_]
  (page
   [:div
    [:div {:class "text-4xl"} "new task"]
    [:form {:method "post"}
     (h/raw (anti-forgery-field))
     [:textarea {:class ""
                 :placeholder "new task"
                 :name "task"}]
     [:input {:name "week" :value 1}]
     [:input {:name "num" :value 1}]
     [:button "create"]]]))

(defn create-task! [^long week ^long num ^String task]
  (put! [{:db/add -1
          :week week
          :num  num
          :task task
          :issued (now)}]))

(defn create! [{{:keys [week num task]} :params}]
  (t/log! {:level :info :data {:week week :num num :task task}} "create!")
  (create-task! (parse-long week) (parse-long num) task)
  (resp/redirect "/admin"))

(defn edit [_]
  (under-construction-page nil))

(defn edit! [_]
  (under-construction-page nil))

;; no delete?
(defn delete! [_]
  (under-construction-page nil))
