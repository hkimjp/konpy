(ns konpy.goods-bads
  (:require
   [clojure.pprint :refer [pprint]]
   [konpy.carmine :as c]))

(defn who-sent [what]
  (let [eids (map #(re-find #"\d+" %) (c/keys (str "kp:*:" what)))]
    (flatten (for [eid eids]
               (let [key (str "kp:" eid ":" what)]
                 (c/lrange key))))))

(defn who-sent-how-many [what]
  (->> (who-sent what)
       (group-by identity)
       (map (fn [[k v]] [k (count v)]))))

(comment
  (pprint
   (reverse (sort-by (fn [x] (second x)) (who-sent-how-many "good"))))
  (pprint
   (reverse (sort-by (fn [x] (second x)) (who-sent-how-many "bad"))))
  :rcf)

; (reverse (sort-by (fn [x] (second x)) (who-sent-how-many "good")))

; (reverse (sort-by (fn [x] (second x)) (who-sent-how-many "bad")))
