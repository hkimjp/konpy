(ns konpy.assignments
  (:require [taoensso.telemere :as t]
            [konpy.views :refer [page]]
            [konpy.db :refer [put! q pull entity]]
            [hiccup2.core :as h]
            [ring.util.response :as resp]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [environ.core :refer [env]]))

(defn- now []
  (java.util.Date.))

(defn under-construction [_]
  (page
   [:div {:class ""} "under construction"]))

(defn login [request]
  (get-in request [:session :identity]))

(defn task [request]
  (page
   [:div
    [:div {:class ""} "list"]
    [:div "user:" (login request)]]))

(defn tasks [_]
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
         [:p (str "週" week " 番号 " num " 課題 " task " 〆切 " deadline)])
       [:p [:a {:href "/admin/new"} "new"]]]])))

; (pull 8)

(defn all [_]
  (under-construction nil))

(defn edit [_]
  (under-construction nil))

(defn edit! [_]
  (under-construction nil))

(defn delete! [_]
  (under-construction nil))

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
     [:button "create"]]]))

(defn create! [{{:keys [num week task deadline]} :params}]
  (t/log! {:level :info :data {:task task :deadline deadline}} "create!")
  (put! [{:db/add -1
          :week (parse-long week)
          :num  (parse-long num)
          :task task
          :deadline deadline
          :issued (now)}])
  (resp/redirect "/admin/"))

