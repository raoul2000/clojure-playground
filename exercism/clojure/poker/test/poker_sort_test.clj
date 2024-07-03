(ns poker-sort-test
  (:require [clojure.test :refer [deftest testing is are]]
            [poker-sort :as p]))

(deftest card-value-test
  (are [value card] (= value (p/card-value card true))
    1 "AX"
    2 "2X"
    9 "9X"
    10 "10Z"
    11 "JA"
    12 "QX"
    13 "KX")
  ;; ACE ends
  (are [value card] (= value (p/card-value card false))
    14 "AX"
    2 "2X"
    9 "9X"
    10 "10Z"
    11 "JA"
    12 "QX"
    13 "KX"))

(deftest one-pair?-test
  (testing "hand contains one pair only"
    (is (= false (p/one-pair? "")))
    (is (= false (p/one-pair? "2X 3B JX")))
    (is (= true  (p/one-pair? "2X 3B 2X")))))


(deftest two-pair?-test
  (testing "hand contains two pair only"
    (is (= false (p/two-pair? "")))
    (is (= false (p/two-pair? "2X 3B JX")))
    (is (= false (p/two-pair? "2X 3B 2X")))
    (is (= true  (p/two-pair? "2X 3B 2X 3T")))))

(deftest three-of-a-kind?-test
  (testing "hand contains three of a kind"
    (is (= false (p/three-of-a-kind? "")))
    (is (= false (p/three-of-a-kind? "2X 3B JX")))
    (is (= false (p/three-of-a-kind? "2X 3B 2X")))
    (is (= false (p/three-of-a-kind? "2X 3B 2X 3T")))
    (is (= true  (p/three-of-a-kind? "2X 3B 2X 3T 2U")))))

(deftest straight?-test
  (testing "hand contains straight"
    (is (= false (p/straight? "2X 3B JX 4X 5E")))
    (is (= true  (p/straight? "4R AE 3T 2E 5T")))))


