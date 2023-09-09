(ns poker-test
  (:require [clojure.test :refer [deftest is testing are]]
            [poker :as p]))


(deftest normalize-card-test
  (testing "single card normalization"
    (are [x y] (= x y)
      [11 "C"] (p/normalize-card "JC")
      [12 "H"] (p/normalize-card "QH")
      [13 "C"] (p/normalize-card "KC")
      [1 "C"] (p/normalize-card "1C")
      [10 "D"] (p/normalize-card "10D"))))

(deftest normalize-hand-test
  (testing "hand normalization"
    (are [x y]  (= x y)
      [[4 "S"] [3 "D"]] (p/normalize-hand "4S 3D")
      [[4 "S"] [3 "D"] [13 "H"] [3 "C"]] (p/normalize-hand "4S 3D KH 3C"))))

(deftest n-of-a-kind-test
  (testing "predicate N of a kind"
    (are [x y] (= x y)
      true  (p/n-of-a-kind? 3 [[1 "C"] [1 "H"] [2 "C"] [1 "S"]])
      false (p/n-of-a-kind? 3 [[3 "C"] [1 "H"] [2 "C"] [1 "S"]])
      true  (p/n-of-a-kind?  2 [[3 "C"] [1 "H"] [2 "C"] [1 "S"]])
      true  (p/n-of-a-kind?  2 [[3 "C"] [1 "H"] [3 "S"] [1 "S"]])
      false (p/n-of-a-kind?  2 [[4 "C"] [5 "H"] [3 "S"] [1 "S"]])))) 