(ns konpy.carmine
  (:refer-clojure :exclude [set get keys])
  (:require
   [clojure.string :as str]
   [java-time.api :as jt]
   [taoensso.carmine :as car]
   [taoensso.telemere :as t]))

(defonce my-conn-pool (car/connection-pool {}))
(def     my-conn-spec {:uri "redis://localhost:6379"})
;; container
;; (def     my-conn-spec {:uri "redis://redis.redis7.orb.local:6379"})
(def     my-wcar-opts {:pool my-conn-pool, :spec my-conn-spec})

(defmacro wcar* [& body] `(car/wcar my-wcar-opts ~@body))

(defn set [key value]
  (wcar* (car/set key value)))

(defn setex [key expire value]
  (wcar* (car/setex key expire value)))

(defn get [key]
  (wcar* (car/get key)))

(defn keys [key]
  (wcar* (car/keys key)))

(defn ttl [key]
  (wcar* (car/ttl key)))

(defn lpush [key element]
  (wcar* (car/lpush key element)))

(defn lrange
  ([key] (lrange key 0 -1))
  ([key start stop]
   (wcar* (car/lrange key start stop))))

(defn llen [key]
  (wcar* (car/llen key)))

;-----------------------
(defn- put-key
  [key user ttl]
  (let [now (jt/format "yyyy-MM-dd HH:mm:ss" (jt/local-date-time))]
    (t/log! {:level :debug
             :data {:key key
                    :user user
                    :ttl ttl
                    :now now}}
            "put-key")
    (setex (str key user) ttl now)))

(defn put-login
  [user ttl]
  (put-key "kp:login:" user ttl))

(defn put-answer
  [user ttl]
  (put-key "kp:answer:" user ttl))

(defn put-last-answer
  [answer]
  (t/log! :debug (str "put-last-answer: " answer))
  (set "kp:last-answer" answer))

(defn get-last-answer
  []
  (get "kp:last-answer"))

(defn- get-key
  [key]
  (let [keys (keys (str key "*"))
        pat (re-pattern key)]
    (->> (map (fn [k] [k (get k)]) keys)
         (sort-by second)
         reverse
         (map first)
         (mapv #(str/replace % pat "")))))

(defn get-logins []
  (get-key "kp:login:"))

(defn get-answers []
  (get-key "kp:answer:"))

(defn answered-time [key]
  (get (str "kp:answer:" key)))

(defn logined-time [key]
  (get (str "kp:login:" key)))
