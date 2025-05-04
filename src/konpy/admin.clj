(ns konpy.admin
  (:require [taoensso.telemere :as t]
            [hiccup2.core :as h]
            [ring.util.response :as resp]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            ; [environ.core :refer [env]]
            [konpy.db :refer [put! q]]
            [konpy.utils :refer [now under-construction-page]]
            [konpy.views :refer [page]]))

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
      (for [{:keys [e week num task issued]} ret]
        [:div {:class "flex"}
         [:form {:class "mx-xl"}
          [:div {:class "flex items-center"}
           [:input {:type "hidden" :name "e" :value e}]
           [:input {:class "text-center size-10 shadow-lg outline outline-black/5"
                    :value week}]
           " - "
           [:input {:class "text-center size-10 shadow-lg outline outline-black/5"
                    :value num}]
           [:textarea {:class "w-120 outline outline-black/5 shadow-lg"}
            task]
           [:button {:class "rounded-full bg-sky-200 hover:bg-sky-500 active:bg-red-500"}
            "update"]]]])
      [:div
       [:p [:a {:href "/admin/new"
                :class "text-2xl p-6 rounded-sm bg-sky-200 hover:bg-sky-500 active:bg-red-500"}
            "new"]]]])))

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

(defn put-task! [^long week ^long num ^String task]
  (put! [{:db/add -1
          :week week
          :num  num
          :task task
          :issued (now)}]))

(defn create! [{{:keys [week num task]} :params}]
  (t/log! {:level :info :data {:task task}} "create!")
  (put-task! (parse-long week) (parse-long num) task)
  (resp/redirect "/admin"))

(defn edit [_]
  (under-construction-page nil))

(defn edit! [_]
  (under-construction-page nil))

(defn delete! [_]
  (under-construction-page nil))
