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
  (let [tasks-q '[:find ?week ?num ?task ?issued
                  :keys week num task issued
                  :where
                  [?e :week ?week]
                  [?e :num ?num]
                  [?e :task ?task]
                  [?e :issued ?issued]]
        ret (q tasks-q)]
    (page
     [:div
      [:div
       (for [{:keys [week num task issued]} ret]
         [:p (str week "-" num " " issued " " task)])]
      [:div
       [:p [:a {:href "/admin/new"} "new"]]]])))

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
     [:input {:type "submit" :value "create"}]]]))

;; week num string?
(defn put-task! [week num task]
  (put! [{:db/add -1
          :week (parse-long week)
          :num  (parse-long num)
          :task task
          :issued (now)}]))

(defn create! [{{:keys [week num task]} :params}]
  (t/log! {:level :info :data {:task task}} "create!")
  (put-task! week num task)
  (resp/redirect "/admin/"))

(defn edit [_]
  (under-construction-page nil))

(defn edit! [_]
  (under-construction-page nil))

(defn delete! [_]
  (under-construction-page nil))
