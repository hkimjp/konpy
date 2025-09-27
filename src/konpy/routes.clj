(ns konpy.routes
  (:require
   [clojure.java.io :as io]
   [reitit.ring :as rr]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [taoensso.telemere :as t]
   ;
   [konpy.tasks :as tasks]
   [konpy.admin :as admin]
   [konpy.answer :as answer]
   [konpy.pg :as pg]
   [konpy.login :refer [login-page login! logout!]]
   [konpy.middleware :as m]
   ;
   [konpy.example :as example]))

(defn routes
  []
  [["/assets/*" (rr/create-resource-handler
                 {:path "/" :root "public"})]
   #_["/favicon.ico" (constantly (slurp (io/resource "public/favicon.ico")))]
   ["/" {:get  {:handler login-page}
         :post {:handler login!}}]
   ["/logout" logout!]
   ["/tasks" {:middleware [[m/wrap-users]]}
    ["" tasks/tasks-this-week]
    ["/all" tasks/tasks-all]]
   ["/answer/:e" {:middleware [[m/wrap-users]]}
    [""
     {:get  {:handler answer/answer}
      :post {:handler answer/answer!}}]
    ["/self" answer/answers-self]
    ["/others"  answer/answers-others]]
   ["/answers" {:middleware [[m/wrap-users]]}
    ["/answers" answer/recent-answers]
    ["/logins"  answer/recent-logins]
    ["/one/:e" {:get answer/answers-one}]]
   ["/admin" {:middleware [[m/wrap-admin]]}
    ["" {:get  {:handler admin/tasks}
         :post {:handler admin/upsert!}}]
    ["/gc" {:post {:handler admin/gc}}]]

   ["/last-answer" {:get {:handler answer/this-weeks-last-answer}}]
   ["/black" {:get {:handler answer/black}}]

   ["/answer-good" {:post {:handler answer/good}}]
   ["/answer-bad"  {:post {:handler answer/bad}}]
   ["/download"    {:post {:handler answer/download}}]
   ["/q-a"         {:post {:handler pg/q-a}}]

   ["/example"
    ["" {:get  {:handler example/example-page}
         :post {:handler example/example-post}}]
    ["/confirm" {:get {:handler example/example-confirm}}]]])

(defn not-found-handler
  [_]
  {:status 404
   :headers {"Content-Type" "text/html"}
   :body "<h1 style='color:red;'>Not Found</h1>"})

(defn root-handler
  [request]
  (t/log! :info (str (:request-method request) " - " (:uri request)))
  (let [handler (rr/ring-handler
                 (rr/router (routes))
                 #'not-found-handler
                 {:middleware [[wrap-defaults site-defaults]]})]
    (handler request)))
