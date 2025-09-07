(ns user
  (:require
   ; [environ.core :refer [env]]
   ; [java-time.api :as jt]
   ; [konpy.carmine :as c]
   ; [konpy.utils :as u]
   [clj-reload.core :as reload]
   [environ.core :refer [env]]
   [konpy.admin :refer [upsert-task!]]
   [konpy.db :as db]
   [konpy.system :as system]
   [taoensso.telemere :as t]))

(reload/init
 {:dirs ["src" "dev" "test"]
  :no-reload '#{user}})

(defn reload []
  (reload/reload))

; (System/getenv "L22")
; (env :l22)

(comment
  (reload)
  (system/stop-system)
  :rcf)

(t/set-min-level! :debug)

(system/restart-system)

(defn seeds-in
  [week seeds]
  (let [c (atom 0)]
    (doseq [s seeds]
      (swap! c inc)
      (upsert-task! -1 week @c s))))

(comment
  (seeds-in 4
            ["Python で 1/1, 1/2, 1/3, 1/4, 1/5, 1/6, 1/7, 1/8, 1/9, 1/10をプリントしなさい。"
             "Python で apple, orange, banana, grape, melon, peach, pine を 一行に一つずつプリントしなさい。"
             "Python で |, /, -, \\, |, /, -, \\ をプリントしなさい。"
             "print( ) で九九の表をプリントしなさい。"
             "数字がきちんと並ぶように改良しなさい。"
             "数字の後にコンマ(,)を表示しなさい。"
             "マークダウンで表を作る方法をネットで調べる。"
             "九九の表をマークダウンでプリントする."])
  :rcf)
;--------------------------
