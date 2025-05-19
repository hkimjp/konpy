(ns konpy.answer
  (:require
   ; [clojure.java.io :as io]
   ; [clojure.string :as str]
   [environ.core :refer [env]]
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [ring.util.response :as resp]
   [taoensso.telemere :as t]
   ;
   [konpy.carmine :as c]
   [konpy.db :as db]
   [konpy.typing-ex :as typing-ex]
   [konpy.utils :refer [user kp-sha1 now shorten develop?]]
   [konpy.views :refer [page render]]))

(def ^:private btn  "p-1 rounded-xl text-white bg-sky-500 hover:bg-sky-700 active:bg-red-500")

(def ^:private lime "p-1 rounded-xl text-white bg-lime-500 hover:bg-lime-700 active:bg-red-500")

(def look "p-1 text-white bg-blue-500 hover:bg-blue-700 active:bg-red-500")

(def ^:private te "my-2 p-2 text-md font-mono grow h-60 outline outline-black")

(def ^:private q-find-answers
  '[:find ?answer ?updated ?identical ?e
    :keys answer updated identical e
    :in $ ?author ?tid
    :where
    [?e :author ?author]
    [?e :task/id ?tid]
    [?e :answer ?answer]
    [?e :identical ?identical]
    [?e :updated ?updated]])

(def ^:private q-find-author
  '[:find ?author
    :in $ ?sha1
    :where
    [?e :author ?author]
    [?e :sha1 ?sha1]])

; typing-ex
(def ^:private q-answers-self
  '[:find ?answer ?updated ?identical ?author  ?typing-ex
    :keys answer updated identical author  typing-ex
    :in $ ?tid ?author
    :where
    [?e :task/id ?tid]
    [?e :author ?author]
    [?e :answer ?answer]
    [?e :updated ?updated]
    [?e :identical ?identical]
    [?e :typing-ex ?typing-ex]])

; typing-ex
(def ^:private q-answers-others
  '[:find ?answer ?updated ?author ?identical ?typing-ex
    :keys answer updated author identical typing-ex
    :in $ ?tid
    :where
    [?e :task/id ?tid]
    [?e :author ?author]
    [?e :answer ?answer]
    [?e :updated ?updated]
    [?e :identical ?identical]
    [?e :typing-ex ?typing-ex]])

;; can not [?e :db/id ?tid]
(def q-week-num
  '[:find ?week ?num
    :keys week num
    :in $ ?tid
    :where
    [?e :db/id ?tid]
    [?e :week ?week]
    [?e :num ?num]])

;-------------------------

(defn find-answers
  [author tid]
  (db/q q-find-answers
        author tid))

(defn last-answer
  "if no answer, returns nil."
  [author tid]
  (last (sort-by :updated (find-answers author tid))))

(defn identical
  "returns a list of author's login whose answer's sha1　is equal to `sha1`."
  [sha1]
  (->> (db/q q-find-author
             sha1)
       (mapv first)))

(defn answer
  [{{:keys [e]} :path-params :as request}]
  (let [tid (parse-long e)
        task (db/pull tid)
        user (user request)
        last-answer (last-answer user tid)]
    (t/log! {:level :debug
             :data {:tid tid
                    :user user
                    :last-answer (shorten last-answer)}}
            "answer")
    (page
     [:div.mx-4
      [:div [:span {:class "font-bold"} "課題: "] (:task task)]
      [:form
       {:hx-confirm "ほんとに？"
        :hx-post (str "/answer/" e)
        :hx-target "#body"
        :hx-swap "outerHTML"}
       (h/raw (anti-forgery-field))
       [:input {:type "hidden" :name "e" :value tid}]
       (when (some? last-answer)
         [:div "自分の最新回答。もっといい答えができたら再送しよう。"])
       [:div.flex
        [:textarea {:class te :name "answer"}
         (:answer last-answer)]]
       [:div [:button {:class btn :type "submit"}
              (if (some? last-answer)
                "再送"
                "送信")]]]
      [:div {:class "flex gap-4 my-2"}
       [:a {:class lime :href (str "/answer/" tid "/self")}
        "自分の回答"]
       (when (some? last-answer)
         [:a {:class lime :href (str "/answer/" tid "/others")}
          "他受講生の回答"])]
      [:div {:class "flex gap-4 my-2"}
       [:a {:class btn :href "/tasks"} "問題に戻る"]]])))

(defn answer!
  [{{:keys [e answer]} :params :as request}]
  (let [tid (parse-long e)
        sha1 (kp-sha1 answer)
        identical (identical sha1)
        user (user request)
        avg (typing-ex/average user)]
    (t/log! {:level :debug
             :data {:user user
                    :typing-ex avg
                    :tid tid
                    :sha1 sha1
                    :identical identical}}
            "answer!")
    (try
      (db/put! [{:db/add -1
                 :task/id tid
                 :author user
                 :answer answer
                 :sha1 sha1
                 :updated (now)
                 :identical identical
                 :typing-ex avg}])
      (c/put-answer user (if (develop?) 10 (* 24 60 60)))
      (resp/redirect "/tasks")
      (catch Exception e
        (t/log! :error (.getMessage e))))))

(defn- show-answer
  [a]
  (t/log! :debug (str "show-answer :typing-ex " a))
  [:div.my-8
   ;[:hr.my-2]
   [:div [:span.font-bold "Author: "] (:author a)]
   [:div [:span.font-bold "Date: "] (str (:updated a))]
   [:div [:span.font-bold "Same: "] (print-str (:identical a))]
   [:div [:span.font-bold "Typing: "] (:typing-ex a)]
   [:div [:span.font-bold "WIL: "]
    [:a {:class btn
         :href (str (env :wil) "/last/" (:author a))} "look"]]
   [:div.flex
    [:textarea {:class te} (:answer a)]]])

(defn answers-self
  [{{:keys [e]} :path-params :as request}]
  (let [answers (->> (db/q q-answers-self (parse-long e) (user request))
                     (sort-by :updated)
                     reverse)]
    (page
     [:div {:class "mx-4"}
      (for [a answers]
        (show-answer a))])))

(defn answers-others
  [{{:keys [e]} :path-params}]
  (let [answers (->> (db/q q-answers-others (parse-long e))
                     (sort-by :updated)
                     reverse)]
    (page
     [:div {:class "mx-4 my-2"}
      [:div {:class "text-2xl"} "現在までの回答数(人数): "
       (count answers) " (" (-> (map :author answers) set count) ")"]
      (for [a answers]
        (show-answer a))])))

;------------------------------------------

(defn recent-answers
  [_]
  (let [answers (-> (c/get-answers) print-str)]
    (t/log! :debug answers)
    (render
     [:div#answers answers])))

(defn recent-logins
  [_]
  (let [logins (-> (c/get-logins) print-str)]
    (t/log! :debug logins)
    (render
     [:div#logins logins])))
