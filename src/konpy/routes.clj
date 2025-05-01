;; should devide into assignments and answers?
(ns konpy.routes
  (:require [reitit.ring :as reitit-ring]
            [ring.util.response :as response]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [taoensso.telemere :as t]
            [konpy.example :as example]
            [konpy.login :refer [login-page login-post]]
            [konpy.views :refer [under-construction]]))

; /assets/css ?
(defn routes
  []
  [["/css/*" (reitit-ring/create-resource-handler
              {:path "/" :root "public"})]
   ["/js/*" (reitit-ring/create-resource-handler
             {:path "/" :root "public"})]
   ["/images/*" (reitit-ring/create-resource-handler
                 {:path "/" :root "public"})]
   ["/" {:get {:handler login-page}
         :post {:handler login-post}}]
   ["/example" {:get {:handler example/example-page}
                :post {:handler example/example-post}}]
   ["/assignments"
    ["/" under-construction]]
   ["/answers"
    ["/" under-construction]]])

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

