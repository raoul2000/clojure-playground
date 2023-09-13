(ns poker-test
  (:require [clojure.test :refer [deftest is testing are]]
            [poker :as p]))


(deftest normalize-card-test
  (testing "single card normalization"
    (are [x y] (= x y)
      [11 "C"] (p/normalize-card "JC")
      [12 "H"] (p/normalize-card "QH")
      [13 "C"] (p/normalize-card "KC")
      [1  "C"] (p/normalize-card "1C")
      [10 "D"] (p/normalize-card "10D"))))

(deftest normalize-hand-test
  (testing "hand normalization"
    (are [x y]  (= x y)
      [[4 "S"] [3 "D"]] (p/normalize-hand "4S 3D")
      [[4 "S"] [3 "D"] [13 "H"] [3 "C"]] (p/normalize-hand "4S 3D KH 3C"))))


(deftest card->string-test
  (testing "Converts normalized card into string"
    (are [x y] (= x y)
      "1D" (p/card->string [1 "D"])
      "JD" (p/card->string [11 "D"])
      "QS" (p/card->string [12 "S"])
      "KS" (p/card->string [13 "S"]))))

(deftest Hand->string-test
  (testing "Converts normalized form hand into string"
    (are [x y] (= x y)
      "1D QS"       (p/hand->string [[1 "D"] [12 "S"]])
      "1C 1H 2C 1S" (p/hand->string [[1 "C"] [1 "H"] [2 "C"] [1 "S"]]))))

(deftest n-of-a-kind-test
  (testing "predicate N of a kind"
    (are [x y] (= x y)
      true  (p/n-of-a-kind? 3 [[1 "C"] [1 "H"] [2 "C"] [1 "S"]])
      false (p/n-of-a-kind? 3 [[3 "C"] [1 "H"] [2 "C"] [1 "S"]])
      true  (p/n-of-a-kind?  2 [[3 "C"] [1 "H"] [2 "C"] [1 "S"]])
      true  (p/n-of-a-kind?  2 [[3 "C"] [1 "H"] [3 "S"] [1 "S"]])
      false (p/n-of-a-kind?  2 [[4 "C"] [5 "H"] [3 "S"] [1 "S"]]))))


(deftest assign-score-test
  (testing "Assigning score to poker hands"
    (are [score hand-score]  (= score hand-score)
      3  (p/assign-score (p/normalize-hand "4D 4S 6S 8D 3C"))
      4  (p/assign-score (p/normalize-hand "4D 4S 6S 8D 8C"))
      5  (p/assign-score (p/normalize-hand "4D 8S 6S 8D 8C"))
      6  (p/assign-score (p/normalize-hand "9D 8S 7S 6D 5C"))
      7  (p/assign-score (p/normalize-hand "9S 8S 1S 2S 5S"))
      8  (p/assign-score (p/normalize-hand "9S 9D 9H KS KH"))
      9  (p/assign-score (p/normalize-hand "9S 9D 9H 9S KH"))
      10 (p/assign-score (p/normalize-hand "9S 8S 7S 6S 5S"))
      0  (p/assign-score (p/normalize-hand "4D 5S 6S 8D 3C")))))

(deftest keep-top-score-hands-test
  (testing "Returns best hands"
    (is (= '([3 :hand-b] [3 :hand-d])
           (p/keep-top-score-hands [[1 :hand-a]  [3 :hand-b] [1 :hand-c] [3 :hand-d]])))

    (is (= '([4 :hand-b])
           (p/keep-top-score-hands [[1 :hand-a]  [4 :hand-b] [1 :hand-c] [3 :hand-d]])))

    (is (= '([3 :hand-a] [3 :hand-b] [3 :hand-c] [3 :hand-d])
           (p/keep-top-score-hands [[3 :hand-a]  [3 :hand-b] [3 :hand-c] [3 :hand-d]])))))

(deftest hand-vals-test
  (testing "concat cards val to string"
    (is (= "32"
           (p/hand-vals [[2 :A] [3 :B]])))))

(deftest rank-high-cards-test
  (testing "Returns highest high cards in a coll of hands"
    (is (= '([[7 :a] [2 :b]] [[2 :Y] [7 :X]])
           (p/rank-high-cards [[[2 :a] [4 :b]]
                               [[2 :a] [4 :b]]
                               [[2 :Y] [7 :X]]
                               [[2 :a] [5 :b]]
                               [[7 :a] [2 :b]]])))

    (is (= '([[7 :a] [2 :b]])
           (p/rank-high-cards [[[2 :a] [4 :b]]
                               [[2 :a] [4 :b]]
                               [[2 :a] [5 :b]]
                               [[7 :a] [2 :b]]])))))

(deftest compute-score-for-pair-hands-test
  (testing "compute rank for one or two pair hands"
    (is (= 209
           (p/compute-score-for-pair-hands [[2 :a] [2 :b] [3 :c] [4 :d]])))
    (is (= 505
           (p/compute-score-for-pair-hands [[2 :a] [2 :b] [3 :c] [3 :d]]))))

  (testing "score define complete order over same pair count hands"
    (are [x y] (< x y)
      ;; two pairs
      (p/compute-score-for-pair-hands [[2 :a] [2 :b] [3 :c] [3 :d]])  (p/compute-score-for-pair-hands [[2 :a] [2 :b] [4 :c] [4 :d]])
      (p/compute-score-for-pair-hands [[1 :a] [1 :b] [3 :c] [3 :d]])  (p/compute-score-for-pair-hands [[2 :a] [2 :b] [4 :c] [4 :d]])
      (p/compute-score-for-pair-hands [[3 :a] [3 :b] [5 :c] [5 :d]])  (p/compute-score-for-pair-hands [[3 :a] [3 :b] [6 :c] [6 :d]])
      (p/compute-score-for-pair-hands [[5 :c] [5 :d] [3 :a] [3 :b]])  (p/compute-score-for-pair-hands [[3 :a] [3 :b] [6 :c] [6 :d]])

      ;; one pair
      (p/compute-score-for-pair-hands [[5 :c] [5 :d] [3 :a] [2 :b]])  (p/compute-score-for-pair-hands [[1 :a] [3 :b] [6 :c] [6 :d]])
      (p/compute-score-for-pair-hands [[1 :c] [5 :d] [3 :a] [3 :b]])  (p/compute-score-for-pair-hands [[1 :a] [5 :b] [6 :c] [6 :d]]))))


(deftest pair-count-test
  (testing "count how many pairs in a hand"
    (are [x y] (= x y)
      0 (p/pair-count [[1 :a] [2 :b] [3 :c] [4 :d]])
      1 (p/pair-count [[1 :a] [2 :b] [3 :c] [1 :d]])
      1 (p/pair-count [[3 :a] [2 :b] [3 :c] [1 :d]])
      2 (p/pair-count [[3 :a] [1 :b] [3 :c] [1 :d]])
      2 (p/pair-count [[3 :a] [5 :b] [3 :c] [5 :d]])))) 