(ns poker-sort-test
  (:require [clojure.test :refer [deftest testing is are]]
            [poker-sort :as p]))

(deftest card-value-test
  (are [value card] (= value (p/card-value card true))
    1  "AX"
    2  "2X"
    9  "9X"
    10 "10Z"
    11 "JA"
    12 "QX"
    13 "KX")
  ;; ACE ends
  (are [value card] (= value (p/card-value card false))
    14 "AX"
    2  "2X"
    9  "9X"
    10 "10Z"
    11 "JA"
    12 "QX"
    13 "KX"))

(deftest hand-card-values-test
  (testing "sorted hand card values"
    (is (= '(3 5 8 12) (p/hand-card-values "5Z 8U QR 3E" true)))
    (is (= '(5 5 5  5) (p/hand-card-values "5Z 5U 5R 5E" true)))
    (is (= '(1 2 3  4) (p/hand-card-values "2X 3E 4Y AX" true)))
    (is (= '(2 3 4 14) (p/hand-card-values "4X 3E 2Y AX" false)))))

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


(deftest sort-by-rank-test
  (testing "sort hands by rank"
    (is (= '([:one-pair  1 "2S 4H 6S 4D JH"]
             [:high-card 0 "4S 5H 6C 8D KH"])
           (p/sort-by-rank ["2S 4H 6S 4D JH"
                            "4S 5H 6C 8D KH"])) "one-pair-beats-high-card")))


(deftest hand-score-reducer-test
  (testing "card values reducer"
    (is (=  [[[8 6 4] "4A 8R 6E"]]
            (p/scored-hand-reducer [] [[8 6 4] "4A 8R 6E"]))
        "one item is reduced to itself")

    (is (=  [[[4 6 8] "4A 8R 6E"] [[4 6 8] "4A 8R 6E"]]
            (p/scored-hand-reducer [[[4 6 8] "4A 8R 6E"]] [[4 6 8] "4A 8R 6E"]))
        "item with same values are added to result")

    (is (=  [[[4 6 8] "4A 8R 6E"]]
            (p/scored-hand-reducer [[[4 6 7] "4A 8R 6E"]] [[4 6 8] "4A 8R 6E"]))
        "item lower than result is skipped")

    (is (=  [[[4 6 8] "4A 8R 6E"]]
            (p/scored-hand-reducer [[[4 6 7] "4A 8R 6E"]
                                   [[4 6 7] "4A 8R 6E"]] [[4 6 8] "4A 8R 6E"]))
        "item lower than result is skipped")))


(deftest tie-high-card-test
  (testing "select winner among high cards hands"
    (is (= ["3S 4S 5D 6H JH"]
           (p/tie-high-card ["4D 5S 6S 8D 3C"
                             "2S 4C 7S 9H 10H"
                             "3S 4S 5D 6H JH"]))
        "highest-card-out-of-all-hands-wins")

    (is (= ["3S 4S 5D 6H JH"
            "3H 4H 5C 6C JD"]
           (p/tie-high-card ["4D 5S 6S 8D 3C"
                             "2S 4C 7S 9H 10H"
                             "3S 4S 5D 6H JH"
                             "3H 4H 5C 6C JD"]))
        "a-tie-has-multiple-winners")

    (is (= ["3S 5H 6S 8D 7H"]
           (p/tie-high-card ["3S 5H 6S 8D 7H"
                             "2S 5D 6D 8C 7S"]))
        "multiple-hands-with-the-same-high-cards-tie-compares-next-highest-ranked-down-to-last-card"))) 


