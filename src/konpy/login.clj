(ns konpy.login
  (:require
   [buddy.hashers :as hashers]
   [environ.core :refer [env]]
   [hato.client :as hc]
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [ring.util.response :as resp]
   [taoensso.telemere :as t]
   [konpy.views :refer [page]]))

; for a while. need replace.
(def l22 "https://l22.melt.kyutech.ac.jp")

(defn login-page
  [request]
  (t/log! :info (str "login-page flash: " (:flash request)))
  (page
   [:div.mx-4
    [:div.font-bold.p-2 "LOGIN"]
    (when-let [flash (:flash request)]
      [:div {:class "text-red-500"} flash])
    [:div.p-1
     [:form {:method "post"}
      (h/raw (anti-forgery-field))
      [:input.border-1.border-solid.p-1 {:placeholder "your account" :name "login"}]
      [:input.border-1.border-solid.p-1 {:type "password" :placeholder "password" :name "password"}]
      [:button
       {:class "p-1 text-white bg-sky-500 hover:bg-sky-700 active:bg-red-500"}
       "LOGIN"]]]]))

(defn login!
  [{{:keys [login password]} :params}]
  ; (t/log! :debug (str "login " login " password *"))
  (if (env :develop)
    (do
      (t/log! :info (str "develop mode"))
      (t/log! :info (str "login success: " login))
      (-> (resp/redirect "/tasks")
          (assoc-in [:session :identity] login)))
    (try
      (let [resp (hc/get (str l22 "/api/user/" login)
                         {:timeout 3000 :as :json})]
        (if (and (some? resp)
                 (hashers/check password (get-in resp [:body :password])))
          (do
            (t/log! :info (str "login success: " login))
            (-> (resp/redirect "/tasks")
                (assoc-in [:session :identity] login)))
          (do
            (t/log! :info (str "login failed: " login))
            (-> (resp/redirect "/")
                (assoc :session {} :flash "login failed")))))
      (catch Exception e
        (t/log! :warn (.getMessage e))
        (-> (resp/redirect "/")
            (assoc :session {} :flash "enter login/password"))))))

(defn logout!
  [request]
  (t/log! :info (str "logout " (get-in request [:session :identity])))
  (-> (resp/redirect "/")
      (assoc :session {})))

