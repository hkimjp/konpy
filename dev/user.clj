(ns user
  (:require
   [clj-reload.core :as reload]
   [environ.core :refer [env]]
   [java-time.api :as jt]
   [taoensso.telemere :as t]
   [konpy.admin :refer [upsert-task!]]
   [konpy.db :as db]
   [konpy.typing-ex :as typing-ex]
   [konpy.utils :as u]
   [konpy.system :as system]
   konpy.core-test))

(t/set-min-level! :debug)
(system/restart-system)

(defn seeds-in [week seeds]
  (let [c (atom 0)]
    (doseq [s seeds]
      (swap! c inc)
      (upsert-task! -1 week @c s))))

(comment
  (db/q '[:find ?e
          :where
          [?e :author "sum-lov_13"]])

  (db/pull 879)
  (db/q '[:find ?e
          :where
          [?e :week 6]
          [?e :num 3]])
  ;=> 375
  (db/pull 375)
  (:num (db/pull 375))
  ;
  (system/stop-system)
  (system/start-system)
  (seeds-in 7 seeds-7)
  :rcf)

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

(def seeds-7
  ["yen を dollar にする関数 to_dollar(yen)。レートはネットで調べる。"

   "dollar を yen にする関数 to_yen(dollar, rate)。dollar の他にレートを引数で取る。"

   "\"hellohellohello\" のように、与えられた文字列引数を3個繰り返す文字列を返す関数hello(s)。上例はhello(\"hello\")を実行した結果だ。"

   "\"hello hello hello …\" のように、引数で与えられた文字列 s を第二引数の n の回数繰り返した文字列を返す。… は省略を表す。repeat_n(s, n)."

   "5つの数 a,b,c,d の平均を求める関数。"

   "5つの数 a,b,c,d の標準偏差を求める関数。"

   "引数リスト中、偶数だけをプリントする関数。"

   "引数リストに含まれる偶数だけからなるリストを返す関数。"

   "リスト xs に含まれる要素の数を返す関数 my_len(xs)。"

   "数のリストを前提とし、リストに含まれる数を総和する my_sum(xs)。"

   "リスト中の数の平均を求める関数。"

   "リスト中の数の標準偏差を求める関数。リスト中の要素の数を求める関数。リストの要素はスカラーとして良い。"

   "(*) リスト中の全ての要素の数を求める関数。リスト中にはリストも含まれる。"

   "x % y は何を計算するか？調べ、動作確認の関数を書き、結果を確かめろ。"

   "x // y は何を計算するか？調べ、動作確認の関数を書き、結果を確かめろ。"

   "(*) x^y は何を計算するか？みんなで相談、理解してから答えよう。"

   "next_week_day(day).引数 day には 曜日を表す文字列。次の日の曜日を戻す。"

   "組み込み関数 abs(n) を使わずに、数 n の絶対値を返す my_abs(n)."

   "関数 love(name) を定義せよ。引数の文字列 name が自分の好きな人、球団、サッカー選手、食べ物であれば、\"love <その名前>\"を戻り値とする。そうでなければ \"I don't like\" を返す。"

   "pomodoro をプログラムする。1分おきにチャイムを鳴らし、25分経ったら「遠き山に日は落ちて」をやる。休憩の時間だ。チャイムや「遠き山に日は落ちて」は別物でもよい。"

   "(*) 数当てゲームをプログラムしなさい。]"])
