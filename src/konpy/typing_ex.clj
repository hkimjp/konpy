(ns konpy.typing-ex
  (:require
   [environ.core :refer [env]]
   [pg.core :as pg]))

(def config
  {:host "127.0.0.1"
   :user (env :pg-user)
   :password (env :pg-pass)
   :database "typing_ex"})

(def conn
  (pg/connect config))

(defn average
  [user n]
  (let [q "select avg(pt)::numeric(4,1) from (select pt from results
           where login=$1
           order by id desc
           limit $2)"]
    (-> (pg/execute conn q {:params [user n]})
        first
        :avg)))

; (average "hkimura" 10)



