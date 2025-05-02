(ns konpy.routes
  (:require [reitit.ring :as reitit-ring]
            #_[ring.util.response :as response]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [taoensso.telemere :as t]
            [konpy.assignments :as ka]
            [konpy.example :as example]
            [konpy.login :refer [login-page login! logout!]]
            [konpy.views :refer [under-construction]]
            [konpy.middleware :as km]))

; FIXME: everytime compile.
(defn routes
  []
  [""
   ["/assets/*" (reitit-ring/create-resource-handler
                 {:path "/" :root "public"})]
   ["/example" {:get {:handler example/example-page}
                :post {:handler example/example-post}}]
   ["/" {:get  {:handler login-page}
         :post {:handler login!}}]
   ["/logout" logout!]
   ["/assignments" {:middleware [km/wrap-users]}
    ["/" ka/task]
    #_["/tasks" ka/tasks]]
   ["/answers" {:middleware [km/wrap-users]}
    ["/" under-construction]]
   ["/admin" {:middleware [km/wrap-admin]}
    ["/" ka/tasks]
    ["/edit" {:get ka/edit
              :post ka/edit!}]
    ["/delete" {:delete ka/delete!}]
    ["/new" {:get ka/new
             :post ka/create!}]]])

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

