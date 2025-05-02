(ns konpy.middleware
  (:require [environ.core :refer [env]]
            [ring.util.response :as resp]
            [taoensso.telemere :as t]))

(defn- user [request]
  (get-in request [:session :identity]))

;; refactor
(defn wrap-users
  [handler]
  ; (t/log! :info "wrap-users")
  (fn [request]
    (let [user (user request)]
      (t/log! :info user)
      (handler request)
      (if (some? user)
        (handler request)
        (-> (resp/redirect "/")
            (assoc :session {} :flash "need login"))))))

;; refactor!
(defn wrap-admin [handler]
  ; (t/log! :info "wrap-admin")
  (fn [request]
    (let [user (user request)]
      (t/log! :info (str "admin " (env :admin) " user " user))
      (if (= (env :admin) user)
        (handler request)
        (-> (resp/redirect "/")
            (assoc :session {} :flash "admin only"))))))

