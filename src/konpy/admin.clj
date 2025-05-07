(ns konpy.admin
  (:require [taoensso.telemere :as t]
            [hiccup2.core :as h]
            [ring.util.response :as resp]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [konpy.db :refer [put! q]]
            [konpy.utils :refer [now]]
            [konpy.views :refer [page]]))

(def btn "rounded-xl text-white p-1 bg-sky-500 hover:bg-sky-700 active:bg-red-500")

(def box "text-center size-10 shadow-lg outline outline-black/5")

(defn tasks
  "list all the tasks with upsert buttons."
  [_]
  (let [ret (->> (q '[:find ?e ?week ?num ?task ?issued
                      :keys e week num task issued
                      :where
                      [?e :week ?week]
                      [?e :num ?num]
                      [?e :task ?task]
                      [?e :issued ?issued]])
                 (sort-by (juxt :week :num))
                 vec)]
    (page
     [:div {:class "mx-4"}
      [:div {:class "flex gap-4 my-2"}
       [:a {:href "/tasks" :class btn}  "tasks"]
       [:a {:href "/logout" :class btn} "logout"]]
      [:div
       (for [{:keys [e week num task]}
             (conj ret {:e -1 :week "" :num "" :task ""})]
         [:div {:class "flex"}
          [:form {:class "mx-xl" :method "post"}
           (h/raw (anti-forgery-field))
           [:div {:class "flex items-center"}
            [:input {:type "hidden" :name "e" :value e}]
            [:input {:class box :name "week" :value week}]
            " - "
            [:input {:class box :name "num" :value num}]
            [:textarea {:class "w-120 outline outline-black/5 shadow-lg"
                        :name "task"}
             task]
            [:button {:class btn} "upsert"]]]])]])))

(defn upsert-task! [^long e ^long week ^long num ^String task]
  (put! [{:db/id e
          :week week
          :num  num
          :task task
          :issued (now)}]))

(defn upsert! [{{:keys [e week num task]} :params}]
  (t/log! {:level :info
           :data {:e e
                  :week week
                  :num num
                  :task task}}
          "upsert!")
  (upsert-task! (parse-long e) (parse-long week) (parse-long num) task)
  (resp/redirect "/admin"))
