(ns konpy.admin
  (:require [taoensso.telemere :as t]
            [hiccup2.core :as h]
            [ring.util.response :as resp]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            ; [environ.core :refer [env]]
            [konpy.utils :refer [user now]]
            [konpy.views :refer [page under-construction]]
            [konpy.db :refer [put! q pull entity]]))

(defn tasks
  "list all the tasks.
   edit, delete and add."
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
         [:p (str week "-" num " " deadline " " task)])]
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
     [:input {:name "deadline" :value "2025-12-31"}]
     [:input {:type "submit" :value "create"}]]]))

(defn create! [{{:keys [num week task deadline]} :params}]
  (t/log! {:level :info :data {:task task :deadline deadline}} "create!")
  (put! [{:db/add -1
          :week (parse-long week)
          :num  (parse-long num)
          :task task
          :deadline deadline
          :issued (now)}])
  (resp/redirect "/admin/"))

(defn edit [_]
  (under-construction nil))

(defn edit! [_]
  (under-construction nil))

(defn delete! [_]
  (under-construction nil))
