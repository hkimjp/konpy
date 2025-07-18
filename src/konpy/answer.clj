(ns konpy.answer
  (:require
   [clojure.string :as str]
   [environ.core :refer [env]]
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [ring.util.response :as resp]
   [taoensso.telemere :as t]
   ;
   [konpy.carmine :as c]
   [konpy.db :as db]
   [konpy.pg :as pg]
   [konpy.utils :refer [user kp-sha1 now weeks shorten develop?]]
   [konpy.views :refer [page render]]))

(def btn  "p-1 rounded-xl text-white bg-sky-500 hover:bg-sky-700 active:bg-red-500")

(def lime "p-1 rounded-xl text-white bg-lime-500 hover:bg-lime-700 active:bg-red-500")

(def btn-black "px-1 text-white bg-stone-400 hover:bg-stone-500 active:bg-stone-900")

(def te "my-2 p-2 text-md font-mono grow h-60 outline outline-black")

(def look "p-1 text-white bg-lime-500 hover:bg-lime-700 active:bg-red-500")

(def la "underline text-blue-500 hover:bg-blue-900")

(def sep ["🍄","🍅","🍋","🍏","🍇","🍒"])

(def ^:private q-find-author
  '[:find ?author
    :in $ ?sha1
    :where
    [?e :author ?author]
    [?e :sha1   ?sha1]])

(def q-week-num
  '[:find ?week ?num
    :keys week num
    :in $ ?eid
    :where
    [?e :week ?week]
    [?e :num  ?num]
    [(= ?e ?eid)]])

; task/id についた回答を一網打尽に引っこ抜くのは？
(def ^:private q-find-answers
  '[:find ?answer ?updated ?identical ?e ?week-num
    :keys answer updated identical e week-num
    :in $ ?author ?tid
    :where
    [?e :task/id   ?tid]
    [?e :author    ?author]
    [?e :answer    ?answer]
    [?e :identical ?identical]
    [?e :updated   ?updated]
    [?e :week-num  ?week-num]])

(def ^:private q-answers-self
  '[:find ?answer ?updated ?identical ?author ?typing-ex ?e ?week-num
    :keys answer updated identical author typing-ex e week-num
    :in $ ?tid ?author
    :where
    [?e :task/id   ?tid]
    [?e :author    ?author]
    [?e :answer    ?answer]
    [?e :updated   ?updated]
    [?e :identical ?identical]
    [?e :typing-ex ?typing-ex]
    [?e :week-num  ?week-num]])

(def ^:private q-answers-others
  '[:find ?answer ?updated ?author ?identical ?typing-ex ?e ?week-num
    :keys answer updated author identical typing-ex e week-num
    :in $ ?tid
    :where
    [?e :task/id   ?tid]
    [?e :author    ?author]
    [?e :answer    ?answer]
    [?e :updated   ?updated]
    [?e :identical ?identical]
    [?e :typing-ex ?typing-ex]
    [?e :week-num  ?week-num]])

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
  "returns a list of author's login whose answer's sha1 is equal to `sha1`."
  [sha1]
  (->> (db/q q-find-author
             sha1)
       (mapv first)))

;------------------------------------------

(defn- good-display [logins]
  (apply str (mapv #(str "❤️ " %) logins)))

(defn- bad-display [n]
  (apply str (for [_ (range n)]
               "⚫️ ")))

(defn who-sent-good
  [eid]
  (c/lrange (str "kp:" eid ":good")))

(defn good
  [{{:keys [eid]} :params :as request}]
  (let [user (user request)
        key (str "kp:" eid ":good")]
    (t/log! :info (str "answer/good, good to " eid " from " user))
    (c/lpush key user)
    (resp/response (good-display (c/lrange key)))))

(defn number-of-bads
  [eid]
  (c/llen (str "kp:" eid ":bad")))

(defn bad
  [{{:keys [eid]} :params :as request}]
  (let [user (user request)
        key (str "kp:" eid ":bad")]
    (t/log! :info (str "answer/bad, bad to " eid " from " user))
    (c/lpush key user)
    (resp/response (bad-display (c/llen key)))))

;-----------------------------------------

(defn answer
  [{{:keys [e]} :path-params :as request}]
  (let [tid (parse-long e)
        task (db/pull tid)
        user (user request)
        last-answer (last-answer user tid)]
    ; if last-answer is nil, exception occurs.
    (t/log! {:level :debug
             :data {:tid tid :user user :last-answer (shorten last-answer)}}
            "answer")
    (page
     [:div.mx-4
      [:div [:span {:class "font-bold"} "課題: "] (:task task)]
      [:form
       {:hx-confirm   "自分でやれたか？"
        :hx-encoding  "multipart/form-data"
        :hx-post      (str "/answer/" e)
        :hx-target    "#out"
        :hx-swap      "outerHTML"}
       (h/raw (anti-forgery-field))
       [:input {:type "hidden" :name "e" :value tid}]
       [:input
        {:class  "outline"
         :type   "file"
         :accept ".py, .md"
         :name   "file"}]
       [:button {:class btn} "回答"]]
      [:div#out ""]
      [:div {:class "flex gap-4 my-2"}
       [:a {:class lime :href (str "/answer/" tid "/self")}
        "自分の回答"]
       (when (some? last-answer)
         [:a {:class lime :href (str "/answer/" tid "/others")}
          "受講生の回答"])]
      [:div {:class "flex gap-4 my-2"}
       [:a {:class btn :href "/tasks"} "問題に戻る"]]])))

(defn answer!
  [{{:keys [e]} :params :as request}]
  (t/log! :debug (str "e=" e))
  (let [tid (parse-long e)
        answer (slurp (get-in request [:params :file :tempfile]))
        sha1 (kp-sha1 answer)
        identical (identical sha1)
        user (user request)
        avg (pg/tp-average user)
        num (:num (db/pull tid))
        week-num (-> (db/q q-week-num tid) first)]
    (t/log! {:level :debug
             :data {:user user
                    :typing-ex avg
                    :tid tid
                    :sha1 sha1
                    :identical identical
                    :week-num week-num}}
            "answer!")
    (try
      (db/put! [{:db/add    -1
                 :task/id   tid
                 :author    user
                 :answer    answer
                 :sha1      sha1
                 :updated   (now)
                 :identical identical
                 :typing-ex avg
                 :week-num  week-num}])
      (c/put-answer (str num (get sep (mod (weeks) (count sep))) user)
                    (if (develop?) 60 (* 12 60 60)))
      (c/put-last-answer answer)
      (resp/response "「受講生の回答」ボタンが見えないときは再読み込みで。")
      (catch Exception e
        (t/log! :error (.getMessage e))))))

(defn- a-name [s]
  [:a {:name s} s])

(defn- answer-head
  [a]
  [:div
   [:div [:span.font-bold "Author: "] (-> (:author a) a-name)]
   [:div [:span.font-bold "Date: "] (str (:updated a))]
   [:div [:span.font-bold "Same: "] (print-str (:identical a))]
   [:div [:span.font-bold "Typing: "]
    (str (get-in a [:typing-ex :avg] (:typing-ex a))
         "/"
         (get-in a [:typing-ex :count]))]
   [:div [:span.font-bold "WIL: "]
    [:a {:class look
         :href (str (env :wil) "/last/" (:author a))} "look"]]])

(defn- good-button [eid]
  [:div {:class "flex gap-2"}
   [:form {:hx-post   "/answer-good"
           :hx-target (str "#good-" eid)
           :hx-swap   "innerHTML"}
    (h/raw (anti-forgery-field))
    [:input {:type "hidden" :name "eid" :value eid}]
    [:button "👍 "]]
   [:div {:id (str "good-" eid)}
    (good-display (who-sent-good eid))]])

(defn- bad-button [eid]
  [:div {:class "flex gap-2"}
   [:form {:hx-post   "/answer-bad"
           :hx-target (str "#bad-" eid)
           :hx-swap   "innerHTML"}
    (h/raw (anti-forgery-field))
    [:input {:type "hidden" :name "eid" :value eid}]
    [:button "👎 "]]
   [:div {:id (str "bad-" eid)}
    (bad-display (number-of-bads eid))]])

(defn- qa-num [eid]
  (let [num (c/llen (str "kp:" eid ":qa"))]
    (if (zero? num)
      ""
      (str num))))

(defn- qa-button [eid author week-num]
  (t/log! :debug (str "qa-button " eid "," author "," week-num))
  [:div
   [:form {:class      "flex gap-2"
           :hx-confirm "QAに送信しますか？"
           :hx-post    "/q-a"
           :hx-target  (str "#qa-" eid)
           :hx-swap    "innterHTML"}
    (h/raw (anti-forgery-field))
    [:input {:type "hidden" :name "author" :value author}]
    [:input {:type "hidden"
             :name "week-num"
             :value (str (:week week-num) "-" (:num week-num))}]
    [:input {:type "hidden" :name "eid" :value eid}]
    [:span {:id (str "qa-" eid)} "🤔 " (qa-num eid)]
    [:input {:class "outline grow px-1"
             :placeholder "質問とアドバイス、その他。"
             :name "q"}]
    [:button {:class btn} "to QA"]]
   #_[:div {:id (str "qa-" eid)} " "]])

(defn- download-button [answer]
  [:form {:method "post" :action "/download"}
   (h/raw (anti-forgery-field))
   [:input {:type "hidden" :name "answer" :value answer}]
   [:input {:type "submit" :value "download⇣" :class "underline"}]])

(defn- answer-reactions
  [eid author week-num answer]
  [:div
   (good-button eid)
   (bad-button eid)
   (qa-button eid author week-num)
   (download-button answer)])

(defn- show-answer
  [a]
  (t/log! :debug (str "show-answer" a))
  [:div.py-4
   (answer-head a)
   [:div
    [:pre {:class "my-2 p-2 text-md font-mono grow outline outline-black"}
     (:answer a)]]
   (answer-reactions (:e a) (:author a) (:week-num a) (:answer a))])

(defn answers-self
  [{{:keys [e]} :path-params :as request}]
  (let [answers (->> (db/q q-answers-self (parse-long e) (user request))
                     (sort-by :updated)
                     reverse)]
    (page
     [:div {:class "mx-4"}
      (for [a answers]
        (show-answer a))])))

(defn- inner-link [s]
  [:a.underline {:href (str "#" s)} s])

(defn answers-others
  [{{:keys [e]} :path-params}]
  (let [answers (->> (db/q q-answers-others (parse-long e))
                     (sort-by :updated)
                     reverse)]
    (page
     [:div {:class "mx-4 my-2"}
      [:div {:class "text-2xl"} "現在までの回答数(人数): "
       (count answers) " (" (-> (map :author answers) set count) ")"]
      [:div.py-2 (interpose \space (mapv #(inner-link (:author %)) answers))]
      (for [a answers]
        (show-answer a))])))

;------------------------------------------

(defn recent-logins
  [_]
  (let [[fst & rst] (c/get-logins)]
    ; (t/log! :debug logins)
    (render
     [:div#logins fst "(" (c/logined-time fst) "), "
      (apply str (interpose ", " rst))])))

(defn recent-answers
  [_]
  (let [[fst & rst] (c/get-answers)]
    ; (t/log! :debug answers)
    (render
     [:div#answers
      [:a {:class la :href "/last-answer"}
       (str fst "(" (c/answered-time fst) "), ")]
      (apply str (interpose ", " rst))])))

(defn- hide-chars [s]
  (apply str (for [c s]
               (if (Character/isWhitespace c)
                 c
                 (if (zero? (rand-int 3))
                   "◼️"
                   c)))))

(defn this-weeks-last-answer
  [_]
  (page
   [:div {:class "mx-4"}
    [:div {:class "text-2xl"} "Last Answer:"]
    [:pre {:class "my-2 p-2 text-md font-mono grow outline outline-black"}
     (-> (c/get-last-answer) hide-chars)]]))

;------------------------------------------

(defn- content
  "if str `s` is a Python code, returns `.py`, otherwise `.md`"
  [s]
  (let [lines (str/split-lines s)]
    (if (some #(str/starts-with? % "```") lines)
      ".md"
      (cond
        (some #(str/starts-with? % "def ") lines) ".py"
        (some #(str/starts-with? % "for ") lines) ".py"
        (some #(str/starts-with? % "when ") lines) ".py"
        (some #(str/starts-with? % "if ") lines) ".py"
        (some #(str/starts-with? % "from ") lines) ".py"
        (some #(str/starts-with? % "import ") lines) ".py"
        :else ".md"))))

(defn download
  [{{:keys [answer]} :params :as request}]
  (t/log! :info "download")
  (let [name (str "download" (content answer))]
    {:status 200
     :headers {"Content-disposition" (str "attachment; filename=" name)}
     :body answer}))

;------------------------------------------

(defn black
  [request]
  (t/log! :debug (get-in request [:session :identity]))
  (let [user (get-in request [:session :identity])
        user-key (str "kp:black:" user)]
    (if (c/get user-key)
      (-> (resp/response (str user " (you) is black listed."))
          (resp/header "Content-Type" "text/html"))
      (do
        (c/setex user-key 300 "black")
        (-> (resp/response "black listed!")
            (resp/header "Content-Type" "text/html"))))))
