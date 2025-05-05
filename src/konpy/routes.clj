(ns konpy.routes
  (:require
   [clojure.java.io :as io]
   [reitit.ring :as reitit-ring]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [taoensso.telemere :as t]
   ;
   [konpy.tasks :as tasks]
   [konpy.admin :as admin]
   [konpy.answer :as answer]
   [konpy.login :refer [login-page login! logout!]]
   [konpy.views :refer [yet]]
   [konpy.middleware :as m]
   ;
   [konpy.example :as example]))

; FIXME: everytime compile.
(def routes
  [["/assets/*" (reitit-ring/create-resource-handler
                 {:path "/" :root "public"})]
   ["/favicon.ico" (constantly (slurp (io/resource "public/favicon.ico")))]
   ["/" {:get  {:handler login-page}
         :post {:handler login!}}]
   ["/logout" logout!]
   ["/tasks" {:middleware [[m/wrap-users]]}
    ["" tasks/tasks-this-week]
    ["/yet" yet]
    ["/all" tasks/tasks-all]]
   ["/answer/:eid" {:middleware [[m/wrap-users]]}
    [""
     {:get  {:handler answer/answer}
      :post {:handler answer/answer!}}]]
   ["/admin" {:middleware [[m/wrap-admin]]}
    ["" {:get {:handler admin/tasks}}]
    ["/new" {:get  {:handler admin/new}
             :post {:handler admin/create!}}]
    ["/edit/:n" {:get  admin/edit
                 :post admin/edit!}]
    ["/delete/:n" {:delete admin/delete!}]]
   ;
   ["/example"
    ["" {:get  {:handler example/example-page}
         :post {:handler example/example-post}}]
    ["/confirm" {:get {:handler example/example-confirm}}]]])

(defn not-found-handler
  [_]
  {:status 404
   :headers {"Content-Type" "text/html"}
   :body "<h1 style='color:red;'>Not Found</h1>"})

(def app
  (reitit-ring/ring-handler
   (reitit-ring/router routes)
   #'not-found-handler
   {:middleware [[wrap-defaults site-defaults]]}))


