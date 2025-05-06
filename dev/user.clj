(ns user
  (:require
   [clj-reload.core :as reload]
   [environ.core :refer [env]]
   [java-time.api :as jt]
   [taoensso.telemere :as t]
   [konpy.admin :refer [upsert-task!]]
   [konpy.db :as db]
   [konpy.utils :as u]
   [konpy.system :as system]
   konpy.core-test))

(t/set-min-level! :info)

(system/restart-system)

(def seeds-4
  ["タイピング練習を50回こなす"
   "タイピング練習で最高点10点以上とる"
   "VScode の背景色を明るく、あるいは逆に暗くするには？"
   "VScode で作るテキストファイルの文字の大きさを変えるには？"
   "Python で 1/1, 1/2, 1/3, 1/4, 1/5, 1/6, 1/7, 1/8, 1/9, 1/10をプリントしなさい。"
   "Python で apple, orange, banana, grape, melon, peach, pine を 一行に一つずつプリントしなさい。"
   "Python で |, /, -, \\, |, /, -, \\ をプリントしなさい。"
   ,
   "print( ) で九九の表をプリントしなさい。"
   "数字がきちんと並ぶように改良しなさい。"
   "数字の後にコンマ(,)を表示しなさい。"
   "マークダウンで表を作る方法をネットで調べる。"
   "九九の表をマークダウンでプリントする。"])

(def seeds-5
  ["5週目の課題"
   "Beep()を鳴らす"
   "Beep()できらきら星"])

(defn seeds-in [week seeds]
  (let [c (atom 0)]
    (doseq [s seeds]
      (swap! c inc)
      (upsert-task! -1 week @c s))))

(comment
  (seeds-in 4 seeds-4)
  (seeds-in 5 seeds-5)

  (system/stop-system)
  (reload/reload)

  (db/q '[:find ?author
          :in $ ?to
          :where
          [?e :author ?author]
          [?e :sha1 ?to]]
        -6652132719765422345036288287526973911102942116N)

  (db/q '[:find ?author
          :in $ ?x
          :where
          [?e :answer ?answer]
          [?e :author ?author]
          [?e :sha1 ?sha1]
          [(= ?sha1 ?x)]]
        "356a192b7913b04c54574d18c28d46e6395428ab")

  :rcf)

(comment

  (def x [{:foo 2 :bar 11}
          {:bar 99 :foo 1}
          {:bar 55 :foo 2}
          {:foo 1 :bar 77}])

  (defn f [^long x ^long y]
    (+ x y))

  (sort-by (juxt :foo :bar) x)

  (db/q '[:find ?e ?week ?num ?task ?issued
          :in $ ?week
          :where
          [?e :week ?week]
          [?e :num ?num]
          [?e :task ?task]
          [?e :issued ?issued]]
        5)

  (u/weeks)

  (jt/local-date)
  (jt/local-date 2025 3 5)
  (jt/instant)

  (def start-date (jt/local-date 2025 3 31))
  (def today (jt/local-date))

  start-date
  today
  (jt/gap (jt/local-date) start-date)

  (env :port)

  (reload/reload)

  (system/restart-system)

  (db/conn?)
  (db/start "storage/db.sqlite")
  (db/conn?)

  (def tasks-q '[:find ?e ?num ?week ?task ?deadline ?issued
                 :in $ ?week
                 :where
                 [?e :num ?num]
                 [?e :week ?week]
                 [?e :task ?task]
                 [?e :deadline ?deadline]
                 [?e :issued ?issued]])

  (db/q tasks-q "1")

  (db/put! [{:db/id -1 :name "hiroshi" :sex "male" :age 63}])

  (db/put! [[:db/add -1 :name "isana"]
            [:db/add -1 :work "police"]
            [:db/add -1 :age 28]])

  (def eid '[:find ?e
             :where
             [?e]])

  (db/q eid)

  (db/pull 3)

  ;; how to use `db/entity`?
  (db/entity 3)

  (def name-age-q '[:find ?name ?age
                    :where
                    [?e :name ?name]
                    [?e :age ?age]])

  (db/q name-age-q)

  (db/q '[:find ?e ?name ?age
          :in $ ?name
          :where
          [?e :name ?name]
          [?e :age ?age]]
        "akari")

  (db/pull ['*] 1)
  (db/pull  [:work] 1)

  (db/pull [:name :age] 1)

  (db/q '[:find (count ?e)
          :where
          [?e _ _]])
  (db/stop)
  (db/conn?)

  :rcf)
