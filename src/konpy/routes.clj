;; should devide into assignments and answers?
(ns konpy.routes
  (:require [reitit.ring :as reitit-ring]
            #_[ring.util.response :as response]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [taoensso.telemere :as t]
            [konpy.example :as example]
            [konpy.login :refer [login-page login!]]
            [konpy.views :refer [under-construction]]
            [konpy.middleware :as km]))

; FIXME: everytime compile.
(defn routes
  []
  [""
   ["/assets/*" (reitit-ring/create-resource-handler
                 {:path "/" :root "public"})]
   ["/" {:get {:handler login-page}
         :post {:handler login!}}]
   ["/assignments" {:middleware [km/wrap-users]}
    ["/" under-construction]]
   ["/answers" {:middleware [km/wrap-users]}
    ["/" under-construction]]
   ["/admin" {:middleware [km/wrap-admin]}
    ["/assignments/" under-construction]]
   ["/example" {:get {:handler example/example-page}
                :post {:handler example/example-post}}]])

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

