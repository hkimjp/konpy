(ns konpy.carmine
  (:refer-clojure :exclude [set get keys])
  (:require
   [clojure.string :as str]
   [taoensso.carmine :as car]
   [java-time.api :as jt]))

(defonce my-conn-pool (car/connection-pool {}))
(def     my-conn-spec {:uri "redis://localhost:6379"})
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

;-----------------------
(defn put-login
  "24hour, jt/local-date-time"
  ([user] (put-login user (* 24 60 60)))
  ([user ex]
   ;; should str?
   (setex (str "kp:login:" user)
          ex
          (jt/format "yyyy-MM-dd hh:mm:ss" (jt/local-date-time)))))

(defn get-logins
  []
  (let [keys (keys "kp:login:*")]
    (->> (map (fn [k] [k (get k)]) keys)
         (sort-by second)
         reverse
         (map first)
         (mapv #(str/replace % #"kp:login:" "")))))

(comment
  (jt/format "yyyy-MM-dd hh:mm:ss" (jt/local-date-time))
  (put-login "hkimura")
  (put-login "hkim")
  (get "kp:login:hkimura")
  (get-logins)
  :rcf)
