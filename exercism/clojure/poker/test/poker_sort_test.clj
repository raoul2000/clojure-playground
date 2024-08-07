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
    (is (= '([:one-pair  7 "2S 4H 6S 4D JH"]
             [:high-card 8 "4S 5H 6C 8D KH"])
           (p/sort-by-category ["2S 4H 6S 4D JH"
                            "4S 5H 6C 8D KH"])) "one-pair-beats-high-card")))

(deftest highest-hands-by-rank-test
  (testing "return highest rank and related hands"
    (is (= [:high-card '("2A 5B 6T 7U 8I" "2A 5B 6T 7U 9I")]
           (p/highest-hands-by-category ["2A 5B 6T 7U 8I" "2A 5B 6T 7U 9I"])))
    (is (= [:one-pair '("7U 4T 3Y 7I JY")]
           (p/highest-hands-by-category ["2A 5B 6T 7U 8I" "2A 5B 6T 7U 9I" "7U 4T 3Y 7I JY"])))
    (is (= [:one-pair '("2A 5B 6T 8U 8I" "7U 4T 3Y 7I JY")]
           (p/highest-hands-by-category ["2A 5B 6T 8U 8I" "2A 5B 6T 7U 9I" "7U 4T 3Y 7I JY"])))
    (is (= [:two-pair '("2A 5B 2T 8U 8I")]
           (p/highest-hands-by-category ["2A 5B 2T 8U 8I" "2A 5B 6T 7U 9I" "7U 4T 3Y 7I JY"])))
    (is (= [:two-pair '("2A 5B 2T 8U 8I" "7U 4T JY 7I JY")]
           (p/highest-hands-by-category ["2A 5B 2T 8U 8I" "2A 5B 6T 7U 9I" "7U 4T JY 7I JY"])))
    (is (= [:three-of-a-kind '("7U 4T JY JI JY")]
           (p/highest-hands-by-category ["2A 5B 2T 8U 8I" "2A 5B 6T 7U 9I" "7U 4T JY JI JY"])))
    (is (= [:three-of-a-kind '("2A 5B 8T 8U 8I" "7U 4T JY JI JY")]
           (p/highest-hands-by-category ["2A 5B 8T 8U 8I" "2A 5B 6T 7U 9I" "7U 4T JY JI JY"])))
    (is (= [:straight '("2Z 4T 5U 3T 6I")]
           (p/highest-hands-by-category ["2Z 4T 5U 3T 6I" "2A 2B 2T 7U 8I"])))
    (is (= [:straight '("2Z 4T 5U 3T 6I" "AA 2B 4T 5U 3I")]
           (p/highest-hands-by-category ["2Z 4T 5U 3T 6I" "2A 2B 2T 7U 8I" "AA 2B 4T 5U 3I"]))
        "straight with ace rank low")
    (is (= [:straight '("AT QT KT J2 10T")]
           (p/highest-hands-by-category ["QZ 4T 5U 3T 6I" "2A 2B 2T 7U 8I" "AT QT KT J2 10T"]))
        "straight with ace rank high")
    (is (= [:flush '("2Z 4Z KZ 7Z 6Z")]
           (p/highest-hands-by-category ["2Z 4Z KZ 7Z 6Z" "2A 2B 2T 7U 8I"])))
    (is (= [:flush '("2Z 4Z KZ 7Z 6Z")]
           (p/highest-hands-by-category ["2Z 4Z KZ 7Z 6Z" "2A 2B 2T 7U 8I" "AT QT KT J2 10T"])))
    (is (= [:flush '("2Z 4Z KZ 7Z 6Z" "2A 4A 5A 7A 8A")]
           (p/highest-hands-by-category ["2Z 4Z KZ 7Z 6Z" "2A 4A 5A 7A 8A" "AT QT KT J2 10T"])))
    (is (= [:full-house '("AT JI AT AF JT")]
           (p/highest-hands-by-category ["2Z 4Z KZ 7Z 6Z" "2A 4A 5A 7A 8A" "AT JI AT AF JT"])))
    (is (= [:full-house '("2A 2A 2T 7H 7I" "AT JI AT AF JT")]
           (p/highest-hands-by-category ["2Z 4Z KZ 7Z 6Z" "2A 2A 2T 7H 7I" "AT JI AT AF JT"])))
    (is (= [:four-of-a-kind '("2Z 2Z 2Z 2T 6Z")]
           (p/highest-hands-by-category ["2Z 2Z 2Z 2T 6Z" "2A 2A 2T 7H 7I" "AT JI AT AF JT"])))
    (is (= [:four-of-a-kind '("2Z 2Z 2Z 2T 6Z" "AT AI AT AF JT")]
           (p/highest-hands-by-category ["2Z 2Z 2Z 2T 6Z" "2A 2A 2T 7H 7I" "AT AI AT AF JT"])))
    (is (= [:straight-flush '("2Z 4Z 5Z 3Z 6Z")]
           (p/highest-hands-by-category ["2Z 4Z 5Z 3Z 6Z" "2A 2B 2T 7U 8I" "AA 2B 4T 5U 3I"]))
        "straight flush with ace rank low")
    (is (= [:straight-flush '("AT QT KT JT 10T")]
           (p/highest-hands-by-category ["QZ 4T 5U 3T 6I" "2A 2B 2T 7U 8I" "AT QT KT JT 10T"]))
        "straight flush with ace rank high")))


;; tie case : several hands having the same rank must be sorted to find a winner


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
           (p/tie-hands ["4D 5S 6S 8D 3C"
                             "2S 4C 7S 9H 10H"
                             "3S 4S 5D 6H JH"]))
        "highest-card-out-of-all-hands-wins")

    (is (= ["3S 4S 5D 6H JH"
            "3H 4H 5C 6C JD"]
           (p/tie-hands ["4D 5S 6S 8D 3C"
                             "2S 4C 7S 9H 10H"
                             "3S 4S 5D 6H JH"
                             "3H 4H 5C 6C JD"]))
        "a-tie-has-multiple-winners")

    (is (= ["3S 5H 6S 8D 7H"]
           (p/tie-hands ["3S 5H 6S 8D 7H"
                             "2S 5D 6D 8C 7S"]))
        "multiple-hands-with-the-same-high-cards-tie-compares-next-highest-ranked-down-to-last-card")))

(deftest hand->scored-hand-test
  (testing "create scored pair"
    (is (= [[1006 7 3 2] "3E 6Y 2E 7U 6U"]
           (p/hand->scored-hand "3E 6Y 2E 7U 6U")))
    (is (= [[1013 7 6 5] "KE 6Y KE 7U 5U"]
           (p/hand->scored-hand "KE 6Y KE 7U 5U")))
    (is (= [[1013 1006 5] "KE 6Y KE 6U 5U"]
           (p/hand->scored-hand "KE 6Y KE 6U 5U"))
        "works also on 2 pairs hand")

    ;; also works for three-of-a-kind
    (is (= [[2006 3 2] "3E 6Y 2E 6U 6U"]
           (p/hand->scored-hand "3E 6Y 2E 6U 6U")))
    (is (= [[2013 7 6] "KE 6Y KE 7U KU"]
           (p/hand->scored-hand "KE 6Y KE 7U KU")))))


(deftest tie-pair-test
  (testing "select among one pair hands"
    (is (= ["2Z 4T 6F 9F 6Y"]
           (p/tie-hands ["2Z 4T 3F 2F 6Y" "2Z 4T 6F 9F 6Y"])))
    (is (= ["2Z 4T 6F KF 6Y"]
           (p/tie-hands ["2Z 4T 3F 2F 6Y" "2Z 4T 6F 9F 6Y" "2Z 4T 6F KF 6Y"])))
    (is (= ["2Z 4T 6F KF 6Y" "2X 4X 6O KU 6H"]
           (p/tie-hands ["2Z 4T 3F 2F 6Y" "2Z 4T 6F 9F 6Y" "2Z 4T 6F KF 6Y" "2X 4X 6O KU 6H"]))
        "many winners")
    ;; two pairs
    (is (= ["2Z 9T 6F 9F 6Y"]
           (p/tie-hands ["2Z 4T 6F 2F 6Y" "2Z 9T 6F 9F 6Y"])))
    (is (= ["5Z 9T 6F 9F 6Y"]
           (p/tie-hands ["2Z 4T 3F 2F 3Y" "2Z 9T 6F 9F 6Y" "5Z 9T 6F 9F 6Y"])))
    (is (= ["5T 9T 6F 9F 6Y" "5Z 9T 6F 9F 6Y"]
           (p/tie-hands ["2Z 4T 3F 2F 3Y" "5T 9T 6F 9F 6Y" "5Z 9T 6F 9F 6Y"]))
        "many winners")))




;; exercism tests //////////////////////////////////////////////////////////////////////////////////////////////////////



(defn f [xs ys] (= (sort (p/best-hands xs)) (sort ys)))

(deftest single-hand-always-wins
  (is (f ["4S 5S 7H 8D JC"] ["4S 5S 7H 8D JC"])))

(deftest highest-card-out-of-all-hands-wins
  (is (f ["4D 5S 6S 8D 3C"
          "2S 4C 7S 9H 10H"
          "3S 4S 5D 6H JH"]
         ["3S 4S 5D 6H JH"])))

(deftest a-tie-has-multiple-winners
  (is (f ["4D 5S 6S 8D 3C"
          "2S 4C 7S 9H 10H"
          "3S 4S 5D 6H JH"
          "3H 4H 5C 6C JD"]
         ["3S 4S 5D 6H JH"
          "3H 4H 5C 6C JD"])))

(deftest multiple-hands-with-the-same-high-cards-tie-compares-next-highest-ranked-down-to-last-card
  (is (f ["3S 5H 6S 8D 7H"
          "2S 5D 6D 8C 7S"]
         ["3S 5H 6S 8D 7H"])))

(deftest one-pair-beats-high-card
  (is (f ["4S 5H 6C 8D KH"
          "2S 4H 6S 4D JH"]
         ["2S 4H 6S 4D JH"])))

(deftest highest-pair-wins
  (is (f ["4S 2H 6S 2D JH"
          "2S 4H 6C 4D JD"]
         ["2S 4H 6C 4D JD"])))

(deftest two-pairs-beats-one-pair
  (is (f ["2S 8H 6S 8D JH"
          "4S 5H 4C 8C 5C"]
         ["4S 5H 4C 8C 5C"])))

(deftest both-hands-have-two-pairs-highest-ranked-pair-wins
  (is (f ["2S 8H 2D 8D 3H"
          "4S 5H 4C 8S 5D"]
         ["2S 8H 2D 8D 3H"])))

(deftest both-hands-have-two-pairs-with-the-same-highest-ranked-pair-tie-goes-to-low-pair
  (is (f ["2S QS 2C QD JH"
          "JD QH JS 8D QC"]
         ["JD QH JS 8D QC"])))

(deftest both-hands-have-two-identically-ranked-pairs-tie-goes-to-remaining-card-kicker
  (is (f ["JD QH JS 8D QC"
          "JS QS JC 2D QD"]
         ["JD QH JS 8D QC"])))

(deftest three-of-a-kind-beats-two-pair
  (is (f ["2S 8H 2H 8D JH"
          "4S 5H 4C 8S 4H"]
         ["4S 5H 4C 8S 4H"])))

(deftest both-hands-have-three-of-a-kind-tie-goes-to-highest-ranked-triplet
  (is (f ["2S 2H 2C 8D JH"
          "4S AH AS 8C AD"]
         ["4S AH AS 8C AD"])))

(deftest with-multiple-decks-two-players-can-have-same-three-of-a-kind-ties-go-to-highest-remaining-cards
  (is (f ["4S AH AS 7C AD"
          "4S AH AS 8C AD"]
         ["4S AH AS 8C AD"])))

(deftest a-straight-beats-three-of-a-kind
  (is (f ["4S 5H 4C 8D 4H"
          "3S 4D 2S 6D 5C"]
         ["3S 4D 2S 6D 5C"])))

(deftest aces-can-end-a-straight-10-J-Q-K-A
  (is (f ["4S 5H 4C 8D 4H"
          "10D JH QS KD AC"]
         ["10D JH QS KD AC"])))

(deftest aces-can-start-a-straight-A-2-3-4-5
  (is (f ["4S 5H 4C 8D 4H"
          "4D AH 3S 2D 5C"]
         ["4D AH 3S 2D 5C"])))

(deftest both-hands-with-a-straight-tie-goes-to-highest-ranked-card
  (is (f ["4S 6C 7S 8D 5H"
          "5S 7H 8S 9D 6H"]
         ["5S 7H 8S 9D 6H"])))

(deftest even-though-an-ace-is-usually-high-a-5-high-straight-is-the-lowest-scoring-straight
  (is (f ["2H 3C 4D 5D 6H"
          "4S AH 3S 2D 5H"]
         ["2H 3C 4D 5D 6H"])))

(deftest flush-beats-a-straight
  (is (f ["4C 6H 7D 8D 5H"
          "2S 4S 5S 6S 7S"]
         ["2S 4S 5S 6S 7S"])))

(deftest both-hands-have-a-flush-tie-goes-to-high-card-down-to-the-last-one-if-necessary
  (is (f ["4H 7H 8H 9H 6H"
          "2S 4S 5S 6S 7S"]
         ["4H 7H 8H 9H 6H"])))

(deftest full-house-beats-a-flush
  (is (f ["3H 6H 7H 8H 5H"
          "4S 5H 4C 5D 4H"]
         ["4S 5H 4C 5D 4H"])))

(deftest both-hands-have-a-full-house-tie-goes-to-highest-ranked-triplet
  (is (f ["4H 4S 4D 9S 9D"
          "5H 5S 5D 8S 8D"]
         ["5H 5S 5D 8S 8D"])))


(deftest with-multiple-decks-both-hands-have-a-full-house-with-the-same-triplet-tie-goes-to-the-pair
  (is (f ["5H 5S 5D 9S 9D"
          "5H 5S 5D 8S 8D"]
         ["5H 5S 5D 9S 9D"])))

(deftest four-of-a-kind-beats-a-full-house
  (is (f ["4S 5H 4D 5D 4H"
          "3S 3H 2S 3D 3C"]
         ["3S 3H 2S 3D 3C"])))

(deftest both-hands-have-four-of-a-kind-tie-goes-to-high-quad
  (is (f ["2S 2H 2C 8D 2D"
          "4S 5H 5S 5D 5C"]
         ["4S 5H 5S 5D 5C"])))

(deftest with-multiple-decks-both-hands-with-identical-four-of-a-kind-tie-determined-by-kicker
  (is (f ["3S 3H 2S 3D 3C"
          "3S 3H 4S 3D 3C"]
         ["3S 3H 4S 3D 3C"])))

(deftest straight-flush-beats-four-of-a-kind
  (is (f ["4S 5H 5S 5D 5C"
          "7S 8S 9S 6S 10S"]
         ["7S 8S 9S 6S 10S"])))

(deftest both-hands-have-straight-flush-tie-goes-to-highest-ranked-card
  (is (f ["4H 6H 7H 8H 5H"
          "5S 7S 8S 9S 6S"]
         ["5S 7S 8S 9S 6S"])))