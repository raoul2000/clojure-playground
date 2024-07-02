(ns poker-3-test
  (:require [clojure.test :refer [deftest testing is]]
            [poker-3 :refer [best-hands highest-cards card-score highest-card-score
                             score-by-card compare-card-score
                             sort-by-card-score]]))



(defn f [xs ys] (= (sort (best-hands xs)) (sort ys)))

(deftest card-score-test
  (is (= (int \1) (card-score "1A")))
  (is (= (int \J) (card-score "JA"))))

(deftest highest-card-test
  (is (= ["3C"] (highest-cards "1A 2B 3C")))
  (is (= ["QC"] (highest-cards "JA 2B QC")))
  (is (= ["QB" "QC"] (highest-cards "JA QB QC"))))


(deftest highest-card-score-test
  (is (= (int \3) (highest-card-score "1Z 2E 3t")))
  (is (= (int \Q) (highest-card-score "QZ JE 3t"))))

(deftest score-by-card-test
  (testing "returns score for a hand"
    (is (= (+ (int \1)
              (int \J)
              (int \2)) (score-by-card "1A JE 2T")))))

(deftest compare-card-score-test
  (testing "compare 2 hands by card by score"
    (is (=  0 (compare-card-score "2A 3B" "3C 2J")))
    (is (= -1 (compare-card-score "4A 3B" "3C 2J")))
    (is (=  1 (compare-card-score "4A 3B" "3C JX")))))

(deftest sort-by-card-score-test
  (testing "sort hands by card score"
    (is (= ["6Z 9Y" "4X 5T"]
           (sort-by-card-score ["4X 5T" "6Z 9Y"])))
    (is (= ["4X 5T" "5Z 4Y"]
           (sort-by-card-score ["4X 5T" "5Z 4Y"])))
    (is (= ["JX 5T" "5Z 4Y"]
           (sort-by-card-score ["JX 5T" "5Z 4Y"])))))







;; ----------------

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

