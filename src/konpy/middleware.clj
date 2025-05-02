(ns konpy.middleware
  (:require [environ.core :refer [env]]
            [ring.util.response :as resp]
            [taoensso.telemere :as t]))

;; refactor
(defn wrap-users
  [handler]
  ; (t/log! :info "wrap-users")
  (fn [request]
    (t/log! :info (str "wrap-users" (:uri request)))
    (if (= "develop" (env :develop))
      (handler request)
      (if (some? (get-in request [:session :identity]))
        (handler request)
        (-> (resp/redirect "/login")
            (assoc :session {} :flash "need login"))))))

;; refactor!
(defn wrap-admin [handler]
  ; (t/log! :info "wrap-admin")
  (fn [request]
    (t/log! :info (:uri request))
    (if (= "develop" (env :develop))
      (handler request)
      (if (= (env :admin) (get-in request [:session :login]))
        (handler request)
        (-> (resp/redirect "/login")
            (assoc :session {} :flash "admin only"))))))

