(ns konpy.middleware
  (:require [environ.core :refer [env]]
            [ring.util.response :as resp]
            [taoensso.telemere :as t]))
;
; FIXME: compile
;

(defn- user [request]
  (get-in request [:session :identity]))

(defn wrap-users
  [handler]
  ; (t/log! :info "wrap-users")
  (fn [request]
    (let [user (user request)]
      (t/log! :debug (str "wrap-users " user))
      (handler request)
      (if (some? user)
        (handler request)
        (-> (resp/redirect "/")
            (assoc :session {} :flash "need login"))))))

(defn wrap-admin [handler]
  ; (t/log! :info "wrap-admin")
  (fn [request]
    (let [user (user request)]
      (t/log! :debug (str "wrap-admin " user))
      (if (= (env :admin) user)
        (handler request)
        (-> (resp/redirect "/")
            (assoc :session {} :flash "admin only"))))))

