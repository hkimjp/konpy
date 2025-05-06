(ns konpy.answer
  (:require
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [ring.util.response :as resp]
   [taoensso.telemere :as t]
   [konpy.db :as db]
   [konpy.utils :refer [user remove-spaces sha1 now]]
   [konpy.views :refer [page]]))

(defn find-answers
  [author tid]
  (db/q '[:find ?answer ?updated
          :keys answer updated
          :in $ ?author ?tid
          :where
          [?e :author ?author]
          [?e :task/id ?tid]
          [?e :answer ?answer]
          [?e :updated ?updated]]
        author tid))

; (find-answers "hkimura" 1)

(defn last-answer
  [author tid]
  (last (sort-by :updated (find-answers author tid))))

; (last-answer "hkimura" 1)

(defn answer
  [{{:keys [e]} :path-params :as request}]
  (let [e (parse-long e)
        task (db/pull e)
        user (user request)
        last-answer (last-answer user e)]
    (t/log! :info (str "e " e " user:" user))
    (page
     [:div
      [:div "課題:" (:task task)]
      [:div
       [:form {:method "post"}
        (h/raw (anti-forgery-field))
        [:input {:type "hidden" :name "e" :value e}]
        [:div [:textarea {:class "w-120 h-60 outline outline-black/5 shadow-lg"
                          :name "answer"}
               (:answer last-answer)]]
        [:div [:button
               {:type  "submit"
                :class "rounded-xl text-white bg-sky-500 hover:bg-sky-700 active:bg-red-500"}
               "送信"]]]]])))

(defn identical
  "returns authors whose answer's sha1 is equal to n."
  [n]
  ["hkimura"])

(defn answer! [{{:keys [e answer]} :params :as request}]
  (let [e (parse-long e)
        sha1 (-> answer remove-spaces sha1)
        identical (identical sha1)]
    (t/log! :info (str "e " e " answer " answer))
    (t/log! :info (str "sha1 " sha1))
    (t/log! :info (str identical))
    (try
      (db/put! [{:db/add -1
                 :author (user request)
                 :task/id e
                 :answer answer
                 :sha1 sha1
                 :updated (now)
                 :identical identical}])
      (resp/redirect "/tasks")
      (catch Exception e (.getMessage e)))))
