(ns konpy.typing-ex
  (:require
   [environ.core :refer [env]]
   [pg.core :as pg]
   [taoensso.telemere :as t]))

(def config
  {:host "127.0.0.1"
   :user (env :pg-user)
   :password (env :pg-pass)
   :database "typing_ex"})

(def conn
  (pg/connect config))

(defn average
  [user]
  (if false ; (env :develop)
    0
    (let [q "select avg(pt)::numeric(4,1), count(pt)
             from (select pt from results
             where login=$1
             and timestamp > now() - interval '1 week')"
          ret (pg/execute conn q {:params [user]})]
      (t/log! :debug (str "average user: " user " ret " ret))
      (-> ret
          first))))
