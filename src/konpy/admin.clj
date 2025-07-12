(ns konpy.admin
  (:require [taoensso.telemere :as t]
   [hiccup2.core :as h]
   [ring.util.response :as resp]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [konpy.db :as d]
   [konpy.utils :refer [now]]
   [konpy.views :refer [page]]))

(def btn "p-1 rounded-xl text-white bg-sky-500 hover:bg-sky-700 active:bg-red-500")

(def btn-admin "p-1 rounded-xl text-white bg-red-500 hover:bg-red-700 active:bg-red-900")

(def box "text-center size-10 shadow-lg outline outline-black/5")

(def te "my-2 p-2 text-md font-mono grow h-60 outline outline-black")

(defn tasks
  "list all the tasks with upsert buttons."
  [_]
  (let [problems (->> (d/q '[:find ?e ?week ?num ?task ?issued
                             :keys e week num task issued
                             :where
                             [?e :week ?week]
                             [?e :num ?num]
                             [?e :task ?task]
                             [?e :issued ?issued]])
                   (sort-by (juxt (fn [x] (* -1 (:week x))) :num))
                   vec)]
    (page
      [:div {:class "mx-4"}
       [:div {:class "flex gap-4 my-2"}
        [:a {:href "/tasks" :class btn}  "tasks"]
        [:form {:hx-post "/admin/gc" :hx-swap "none"}
         (h/raw (anti-forgery-field))
         [:button {:class btn-admin} "GC"]]
        [:a {:href "/logout" :class btn} "logout"]]
       [:div
        (for [{:keys [e week num task]}
              (cons {:e -1 :week "" :num "" :task ""} problems)]
          [:div {:class "flex gap-2 items-center"}
           [:form {:method "post"}
            (h/raw (anti-forgery-field))
            [:div {:class "flex items-center"}
             [:input {:type "hidden" :name "e" :value e}]
             [:input {:class box :name "week" :value week}]
             " - "
             [:input {:class box :name "num" :value num}]
             [:textarea {:class "w-180 h-10 p-2 outline outline-black/5 shadow-lg"
                         :name "task"}
              task]
             [:button {:class btn-admin} "upsert"]]]
           [:div [:a {:href (str "/answer/" e)}
                  [:buttn {:class btn} "answers"]]]])]])))

; using this as seed function from dev/user.clj
(defn upsert-task!
  [^long e ^long week ^long num ^String task]
  (d/put! [{:db/id e
            :week week
            :num  num
            :task task
            :issued (now)}]))

(defn upsert!
  [{{:keys [e week num task]} :params}]
  (t/log! {:level :info
           :data {:e    e
                  :week week
                  :num  num
                  :task task}}
    "upsert!")
  (upsert-task! (parse-long e) (parse-long week) (parse-long num) task)
  (resp/redirect "/admin"))

(defn gc
  [_]
  (t/log! :info "gc")
  (d/gc)
  (resp/response "OK"))
