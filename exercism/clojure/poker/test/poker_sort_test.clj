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

(deftest flush?-test
  (testing "hand is flush"
    (is (= false (p/flush? "1A 10B 9A 8A 7A")))
    (is (= true  (p/flush? "1A 10A 9A 8A 7A")))))

(deftest full-house?-test
  (testing "hand is full house"
    (is (= false (p/full-house? "2A 4A 8A 9X 7X")))
    (is (= false (p/full-house? "2A 2B 10X 10Z 7Q")))
    (is (= true  (p/full-house? "2A 2B 10X 10Z 2Q")))
    (is (= true  (p/full-house? "JA KB JX KZ KQ")))))

(deftest four-of-a-kind?-test
  (testing "hand is four of a kind"
    (is (= false (p/four-of-a-kind? "2A 3B 6B JB 3A")))
    (is (= true  (p/four-of-a-kind? "2A 2B 2X 2C 3A")))))

(deftest straight-flush?-test
  (testing "hand is straight flush"
    (is (= false (p/straight-flush? "2A 3B 6B JB 3A")))
    (is (= false (p/straight-flush? "4R AE 3T 2E 5T"))
        "a straight is not a straight flush")
    (is (= false (p/straight-flush? "1A 10A 9A 8A 7A"))
        "a flush is nopt a straight flush")
    (is (= true  (p/straight-flush? "JA 10A 9A 8A 7A"))))) 




