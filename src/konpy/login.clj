(ns konpy.login
  (:require [taoensso.telemere :as t]
            [konpy.views :refer [page]]
            [buddy.hashers :as hashers]
            [hato.client :as hc]
            [hiccup2.core :as h]
            [ring.util.response :as resp]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [environ.core :refer [env]]))

; for a while. need replace.
(def l22 "https://l22.melt.kyutech.ac.jp")

(defn login-page
  [request]
  (t/log! :info (str "flash " (:flash request)))
  (page
   [:div
    [:div "LOGIN"]
    (when-let [flash (:flash request)]
      [:div {:class "text-red-500"} flash])
    [:div.flex
     [:form {:method "post"}
      (h/raw (anti-forgery-field))
      [:input {:placeholder "your account" :name "login"}]
      [:input {:type "password" :name "password"}]
      [:button
       {:class "bg-sky-100 hover:bg-sky-300 active:bg-red-500"}
       "LOGIN"]]]]))

(defn login!
  [{{:keys [login password]} :params}]
  (t/log! :info (str "login " login " password " password))
  (if (env :develop)
    (-> (resp/redirect "/assignments/")
        (assoc-in [:session :identity] login))
    (try
      (let [resp (hc/get (str l22 "/api/user/" login)
                         {:timeout 3000 :as :json})]
        (if (and (some? resp)
                 (hashers/check password (get-in resp [:body :password])))
          (-> (resp/redirect "/assignments/")
              (assoc-in [:session :identity] login))
          (-> (resp/redirect "/")
              (assoc :session {} :flash "login failed"))))
      (catch Exception e
        (t/log! :warn (.getMessage e))
        (-> (resp/redirect "/")
            (assoc :session {} :flash "server does not respond."))))))

(defn logout!
  [_]
  (-> (resp/redirect "/")
      (assoc :session {})))
