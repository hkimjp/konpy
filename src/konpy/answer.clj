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
  (db/q '[:find ?answer ?updated ?identical ?e
          :keys answer updated identical e
          :in $ ?author ?tid
          :where
          [?e :author ?author]
          [?e :task/id ?tid]
          [?e :answer ?answer]
          [?e :identical ?identical]
          [?e :updated ?updated]]
        author tid))

; (find-answers "hkimura" 1)

(db/pull 19)
(find-answers "abc" 1)
(defn last-answer
  [author tid]
  (last (sort-by :updated (find-answers author tid))))

; (last-answer "hkimura" 1)

(defn identical
  "returns authors whose answer's sha1 is equal to `sha1`."
  [sha1]
  (->> (db/q '[:find ?author
               :in $ ?sha1
               :where
               [?e :author ?author]
               [?e :sha1 ?sha1]]
             sha1)
       (mapv first)))

(comment
  (db/q '[:find ?e
          :where
          [?e :task/id ?t]])
  (identical "ab51f5a4885cbadf8e3e47737d5d9211dd8c9a94")
  (db/pull 16)
  :rcf)

(defn answer
  [{{:keys [e]} :path-params :as request}]
  (let [tid (parse-long e)
        task (db/pull tid)
        user (user request)
        last-answer (last-answer user tid)]
    (t/log! :info (str "last-answer" last-answer))
    (page
     [:div
      [:div "課題:" (:task task)]
      [:div
       [:form {:method "post"}
        (h/raw (anti-forgery-field))
        [:input {:type "hidden" :name "e" :value tid}]
        [:div [:textarea {:class "w-120 h-60 outline outline-black/5 shadow-lg"
                          :name "answer"}
               (:answer last-answer)]]
        [:div "同一回答:" (:identical last-answer)]
        [:div [:button
               {:type  "submit"
                :class "rounded-xl text-white bg-sky-500 hover:bg-sky-700 active:bg-red-500"}
               "送信"]]]]])))

(defn answer! [{{:keys [e answer]} :params :as request}]
  (let [tid (parse-long e)
        sha1 (-> answer remove-spaces sha1)
        identical (identical sha1)]
    (t/log! :info (str "tid " tid " answer " answer))
    (t/log! :info (str "sha1 " sha1))
    (t/log! :info (str "identical " identical))
    (try
      (db/put! [{:db/add -1
                 :task/id tid
                 :author (user request)
                 :answer answer
                 :sha1 sha1
                 :updated (now)
                 :identical identical}])
      (resp/redirect "/tasks")
      (catch Exception e (.getMessage e)))))
