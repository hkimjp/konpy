(ns konpy.answer
  (:require
   ;
   [clojure.java.io :as io]
   [clojure.string :as str]
   ;
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [ring.util.response :as resp]
   [taoensso.telemere :as t]
   [konpy.db :as db]
   [konpy.utils :refer [user remove-spaces sha1 now shorten]]
   [konpy.views :refer [page render]]))

(def btn  "p-1 rounded-xl text-white bg-sky-500 hover:bg-sky-700 active:bg-red-500")
(def lime "p-1 rounded-xl text-white bg-lime-500 hover:bg-lime-700 active:bg-red-500")
(def te   "p-1 text-md font-mono m-2 w-120 h-60 outline outline-black/5 shadow-lg")

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

(defn last-answer
  "if no answer, returns nil."
  [author tid]
  (last (sort-by :updated (find-answers author tid))))

(defn identical
  "returns a list of author's login whose answer's sha1 value is equal to `sha1`."
  [sha1]
  (->> (db/q '[:find ?author
               :in $ ?sha1
               :where
               [?e :author ?author]
               [?e :sha1 ?sha1]]
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
      [:form {;:method "post"
              :hx-confirm "ほんとに？"
              :hx-post (str "/answer/" e)}
       (h/raw (anti-forgery-field))
       [:input {:type "hidden" :name "e" :value tid}]
       [:div [:textarea {:class te :name "answer"}
              (:answer last-answer)]]
       (when-let [same (:identical last-answer)]
         [:div [:span {:class "font-bold"} "同一回答: "] (print-str same)])
       [:div [:button {:class btn} "送信"]]]
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
        sha1 (-> answer remove-spaces sha1)
        identical (identical sha1)]
    (t/log! {:level :debug
             :data {:tid tid
                    :sha1 (shorten 20 sha1)
                    :identical (shorten 20 (str identical))}})
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

(def q-self '[:find ?answer ?updated
              :keys answer updated
              :in $ ?tid ?author
              :where
              [?e :task/id ?tid]
              [?e :author ?author]
              [?e :answer ?answer]
              [?e :updated ?updated]])

(defn answers-self
  [{{:keys [e]} :path-params :as request}]
  (page
   [:div {:class "mx-4"}
    (for [a (->> (db/q q-self (parse-long e) (user request))
                 (sort-by :updated))]
      [:div
       [:p "Date:" (str (:updated a))]
       [:textarea {:class te} (:answer a)]])]))

(def q-others '[:find ?answer ?updated ?author
                :keys answer updated author
                :in $ ?tid
                :where
                [?e :task/id ?tid]
                [?e :author ?author]
                [?e :answer ?answer]
                [?e :updated ?updated]])

(defn answers-others
  [{{:keys [e]} :path-params}]
  (let [answers (->> (db/q q-others (parse-long e))
                     (sort-by :updated)
                     reverse)]
    (page
     [:div {:class "mx-4 my-2"}
      [:div {:class "text-2xl"} "現在までの回答数(人数): "
       (count answers) " (" (-> (map :author answers) set count) ")"]
      (for [a answers]
        [:div {:class "py-2"}
         [:p "From " [:span {:class "font-bold"} (:author a)]
          ", "
          (str (:updated a))]
         [:textarea {:class te} (:answer a)]])])))

(def ra-q '[:find ?author ?updated
            :keys author updated
            :where
            [?e :author ?author]
            [?e :updated ?updated]])

(defn recent-answers
  [{{:keys [n]} :path-params}]
  (t/log! :debug (str (class n)))
  (let [n (parse-long n)
        answers (->> (db/q ra-q)
                     (sort-by :updated)
                     reverse
                     (take n)
                     (mapv :author))]
    (render
     [:div
      (for [a answers]
        [:span a " "])])))

; find 'login success: <login>' in log/konpy.log
; will soon replaced by redis powered function.
; this is a temporally one.
(defn recent-logins
  [{{:keys [n]} :path-params}]
  (t/log! :debug (str "recent-logins " n))
  (let [users (->> (slurp (io/file "log/konpy.log"))
                   str/split-lines
                   (filter #(re-find #"success: " %))
                   (map #(re-find #"success: (.*)" %))
                   (map second)
                   reverse)]
    (t/log! :debug (str users))
    (render
     [:div (str users)])))

(comment
  (let [s "2025-05-08T06:00:22.204853329Z INFO LOG nuc7 konpy.login[47,13] login success: hkimura"]
    (re-find #"success: (.*)" s))

  (->> (slurp (io/file "log/konpy.log"))
       str/split-lines
       (filter #(re-find #"success: " %))
       (map #(re-find #"success: (.*)" %))
       (map second)
       reverse)

  :rcf)
