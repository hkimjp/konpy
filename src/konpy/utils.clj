(ns konpy.utils
  (:require
   [clojure.string :as str]
   [environ.core :refer [env]]
   [java-time.api :as jt]))

(defn develop?
  []
  (= (env :develop) "true"))

; FIXME: tagged literal?
(defn now
  []
  (str (java.util.Date.)))

(defn user [request]
  (get-in request [:session :identity]))

(def start-day (jt/local-date 2025 4 2))

(defn weeks
  "Returns how many weeks have passed since the argument `date`.
   If no argument given, use the `start`day` defnied above."
  ([] (weeks (jt/local-date)))
  ([date]
   (quot (jt/time-between start-day date :days) 7)))

(comment
  (= 4 (weeks (jt/local-date 2025 5 6)))
  (= 5 (weeks (jt/local-date 2025 5 7)))
  :rcf)

; https://groups.google.com/g/clojure/c/Kpf01CX_ClM
(defn create-hash
  [data-barray]
  (.digest (java.security.MessageDigest/getInstance "SHA1") data-barray))

(defn sha1 [s]
  (-> s
      (.getBytes "UTF-8")
      create-hash
      java.math.BigInteger.))

(defn remove-spaces [s]
  (-> s
      (str/replace #" " "")
      (str/replace #"\r\n" "")))

