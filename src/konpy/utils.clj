(ns konpy.utils
  (:require
   [clojure.string :as str]
   [environ.core :refer [env]]
   [java-time.api :as jt]))

(defn shorten
  ([s] (shorten s 40))
  ([s n] (let [pat (re-pattern (str "(^.{" n "}).*"))]
           (str/replace-first s pat "$1..."))))

(defn develop?
  []
  (= (env :develop) "true"))

; FIXME: tagged literal.
(defn now
  []
  (java.util.Date.))

(defn user [request]
  (get-in request [:session :identity]))

(defn admin? [user]
  (= user (env :admin)))

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

(defn- remove-line-comment
  [line]
  (str/replace line #"#.*" ""))

(defn- remove-docstrings
  [line]
  (let [comments (re-pattern "\"\"\".*?\"\"\"")]
    (str/replace line comments "")))

; transducer?
(defn remove-python-comments
  [s]
  (->> s
       str/split-lines
       (map remove-line-comment)
       (apply str)
       remove-docstrings))

(defn remove-spaces [s]
  (-> s
      (str/replace #" " "")
      (str/replace #"\t" "")
      (str/replace #"\r" "")
      (str/replace #"\n" "")))

; https://groups.google.com/g/clojure/c/Kpf01CX_ClM
(defn sha1 [s]
  (->>  (.getBytes s "UTF-8")
        (.digest (java.security.MessageDigest/getInstance "SHA1"))
        (java.math.BigInteger. 1)
        (format "%x")))

(defn kp-sha1 [s]
  (-> s
      remove-python-comments
      remove-spaces
      sha1
      (subs 0 7)))
