(ns konpy.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [konpy.core :refer :all]))

(deftest core-test
  (testing "I success."
    (is (= 0 0))))
