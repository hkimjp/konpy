(ns konpy.answer
  (:require
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [ring.util.response :as resp]
   [taoensso.telemere :as t]
   [konpy.db :as db]
   [konpy.utils :refer [user remove-spaces sha1]]
   [konpy.views :refer [page]]))

(defn answer [{{:keys [e]} :path-params :as request}]
  (t/log! :info (str "e " e " user:" (user request)))
  (page
   [:div
    [:div "課題番号: " e]
    [:div
     [:label "answer:"]
     [:form {:method "post"}
      (h/raw (anti-forgery-field))
      [:input {:type "hidden" :name "e" :value e}]
      [:div [:textarea {:class "w-120 h-60 outline outline-black/5 shadow-lg"
                        :name "answer"
                        :prompt "your answer"}]]
      [:div [:button
             {:type "submit"
              :class "bg-sky-100 hover:bg-sky-500 active:bg-red-500 rounded-2xl"}
             "送信"]]]]]))

(defn identical [n]
  "returns authors whose answer's sha1 is equal to n."
  ["hkimura"])

(defn answer! [{{:keys [e answer]} :params :as request}]
  (t/log! :info (str "e " e " answer " answer))
  (let [sha1 (-> answer remove-spaces sha1)
        identical (identical sha1)]
    (t/log! :info (str "sha1 " sha1))
    (t/log! :info (str identical))
    (try
      (db/put! [{:db/add -1
                 :author (user request)
                 :to (parse-long e)
                 :answer answer
                 :sha1 sha1
                 :identical identical}])
      (resp/redirect "/tasks")
      (catch Exception e (.getMessage e)))))

; (defn answer! [{params :params :as request}]
;   (let [params (dissoc params :__anti-forgery-token)]
;     (put-answer!
;      (merge params (:path-params request) {:user (user request)}))
;     (page
;      [:div
;       [:div "user " (user request)]
;       [:div "params " (str params)]
;       [:div "path-params " (str (:path-params request))]])))

(resp/redirect "/task")


