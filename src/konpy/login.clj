(ns konpy.login
  (:require [taoensso.telemere :as t]
            [konpy.views :refer [page]]))

(defn login-page
  [_]
  (page
   [:div "login"]))

(defn login-post
  [request]
  (t/log! :info "login-post")
  true)
