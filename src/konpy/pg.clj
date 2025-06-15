(ns konpy.pg
  (:require
   [environ.core :refer [env]]
   [pg.core :as pg]
   [taoensso.telemere :as t]
   [konpy.db :as db]
   [konpy.utils :refer [user]]
   [konpy.system :as-alias system]))

(def qa-config
  {:host "127.0.0.1"
   :user (env :pg-user)
   :password (env :pg-pass)
   :database "qa"})

(def qa-conn qa-config)

(defn q-a
  [{{:keys [author q]} :params :as request}]
  (t/log! :debug (str "qa/q-a, q: " q))
  (try
    (let [sql "insert into questions
             (nick, q)
             values
             ($1, $2)"
          ret (pg/execute qa-conn sql {:params [(user request) q]})]
      (t/log! :debug (str "qa/q-a, ret: " ret))
      {:status  200
       :headers {"Content-Type" "text/plain"}
       :body    "QA に送信しました。"})
    (catch Exception e
      (t/log! :error (.getMessage e))
      (throw (Exception. "q-a error.")))))

(def tp-config
  {:host "127.0.0.1"
   :user (env :pg-user)
   :password (env :pg-pass)
   :database "typing_ex"})

(def tp-conn tp-config)

(defn tp-average
  [user]
  (let [q "select avg(pt)::numeric(4,1), count(pt)
             from (select pt from results
             where login=$1
             and timestamp > now() - interval '1 week')"
        ret (pg/execute tp-conn q {:params [user]})]
    (t/log! :debug (str "average user: " user " ret " ret))
    (-> ret
        first)))

