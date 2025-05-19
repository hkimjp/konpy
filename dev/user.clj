(ns user
  (:require
   [clj-reload.core :as reload]
   [environ.core :refer [env]]
   [java-time.api :as jt]
   [taoensso.telemere :as t]
   ;
   [konpy.admin :refer [upsert-task!]]
   [konpy.db :as db]
   [konpy.typing-ex :as typing-ex]
   [konpy.utils :as u]
   [konpy.system :as system]
   konpy.core-test))

(t/set-min-level! :debug)
(system/restart-system)

(comment
  ;
  (defn add-current-typing-to-answer []
    (let [answers (db/q '[:find ?e ?author ?tid
                          :where
                          [?e :author ?author]
                          [?e :task/id ?tid]])]
      (doseq [[e author] answers]
        (let [average (or (typing-ex/average author 10) 0)
              answer (db/pull e)]
          (db/put! [(update answer :typing-ex (fn [_] average))])))))

  (add-current-typing-to-answer)
  (:typing-ex (db/pull 20))
  :rcf)

(comment
  (db/q '[:find ?eid ?author
          :in $ ?author
          :where
          [?eid :task/id 374]
          [?eid :author ?author]]
        "hkimura")

  (db/pull 377)

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
    ["Python でドレミファソラシド演奏する。"
     "Python でベートーベン「運命」の出だしを演奏する。"
     "Python できらきら星を演奏する。"])

  (def seeds-6
    ["授業中に作成した calc.py ファイルを提出しなさい。"
     "授業中に作成した ruff.py ファイルを提出しなさい。"
     "自分の VScode はフォーカスアウトで自動セーブになってるか？どうやって確認したか？"
     "半径 r の円を底面とする高さ h の円錐の表面積を Python で求める手順と、その答え。"
     "\"I am 20 years old.\" をプリントする Python のコード。"
     "He Says, \"I can do it.\" をプリントするために問題となることは？その問題を回避するために必要な工夫は？その工夫を使ってプリントする。"
     "print(\"1, 2, 3\") と print(\"1\",\"2\",\"3\") の違いはなんですか？"
     "果物の名前３つを引数に取り、それらを横一列にした表のマークダウンをプリントする関数。"
     "f(x)=2x に相当する Python の関数を定義するに注意すべきことは？"
     "整数二つを引数に取り、その平均値を返す関数。"
     "def f(x):
   return 10 + x
と
def g(x):
   print(10 + x)
の関数 f, g の違いについて説明しなさい。"
     "上で定義した f, g を使って f(g(3)) および g(f(4)) を実行し、その結果を説明しなさい。"
     "return と print() の違いはなんですか？"])
  :rcf)

(defn seeds-in [week seeds]
  (let [c (atom 0)]
    (doseq [s seeds]
      (swap! c inc)
      (upsert-task! -1 week @c s))))

(comment
  (seeds-in 6 seeds-6)

  (db/gc)

  (db/q '[:find ?sha1
          :in $ ?author
          :where
          [?e :task/id 31]
          [?e :author ?author]
          [?e :sha1 ?sha1]]
        "daisuke")

  #{["66b1d23469ffbb00b32918a33a0ea097ab7a8560"] ["67084ea7526e2e1fce6699cad5bc9d4c06ba6be1"]}
  (db/q '[:find ?sha1
          :in $ ?author
          :where
          [?e :task/id 31]
          [?e :author ?author]
          [?e :sha1 ?sha1]]
        "knt_07")
  :rcf)

(comment

  :rcf)

(comment
  (seeds-in 4 seeds-4)
  (seeds-in 5 seeds-5)

  (db/q '[:find ?e
          :where
          [?e :identical "a"]])

  (:identical (db/pull 29))
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
