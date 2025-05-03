(ns konpy.routes
  (:require [reitit.ring :as reitit-ring]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [taoensso.telemere :as t]
            [konpy.assignments :as a]
            [konpy.admin :as admin]
            [konpy.answers :as answers]
            [konpy.login :refer [login-page login! logout!]]
            [konpy.views :refer [under-construction]]
            [konpy.middleware :as m]
            ;
            [konpy.example :as example]))

; FIXME: everytime compile.
(defn routes
  []
  [""
   ["/assets/*" (reitit-ring/create-resource-handler
                 {:path "/" :root "public"})]
   ["/" {:get  {:handler login-page}
         :post {:handler login!}}]
   ["/logout" logout!]
   ["/assignments" {:middleware [m/wrap-users]}
    ["/" a/task]
    ["/tasks" a/tasks]]
   ["/answers" {:middleware [m/wrap-users]}
    ["/" under-construction]]
   ["/admin" {:middleware [m/wrap-admin]}
    ["/" {:get {:handler admin/tasks}}]
    ["/new" {:get {:handler admin/new}}]
    :post {:handler admin/create!}]
   ["/edit/:n" {:get admin/edit
                :post admin/edit!}]
   ["/delete/:n" {:delete admin/delete!}]
   ;
   ["/example"
    ["" {:get  {:handler example/example-page}
         :post {:handler example/example-post}}]
    ["/confirm" {:get {:handler example/example-confirm}}]]])

(defn not-found-handler
  [_]
  {:status 404
   :body "not found"})

(defn root-handler
  [request]
  (t/log! :info (str (:request-method request) " - " (:uri request)))
  (let [handler (reitit-ring/ring-handler
                 (reitit-ring/router (routes))
                 #'not-found-handler
                 {:middleware [[wrap-defaults site-defaults]]})]
    (handler request)))

