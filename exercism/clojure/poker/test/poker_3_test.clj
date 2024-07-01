(ns poker-3-test
  (:require [clojure.test :refer [deftest testing is]]
            [poker-3 :refer [best-hands highest-cards card-score]]))



(defn f [xs ys] (= (sort (best-hands xs)) (sort ys)))


(deftest card-score-test
  (is (= (int \1) (card-score "1A")))
  (is (= (int \J) (card-score "JA"))))

(deftest highest-card-test
  (is (= ["3C"] (highest-cards ["1A" "2B" "3C"])))
  (is (= ["QC"] (highest-cards ["JA" "2B" "QC"])))
  (is (= ["QB" "QC"] (highest-cards ["JA" "QB" "QC"]))))

(deftest single-hand-always-wins
  (is (f ["4S 5S 7H 8D JC"] ["4S 5S 7H 8D JC"])))

(deftest highest-card-out-of-all-hands-wins
  (is (f ["4D 5S 6S 8D 3C"
          "2S 4C 7S 9H 10H"
          "3S 4S 5D 6H JH"]
         ["3S 4S 5D 6H JH"])))

#_(deftest a-tie-has-multiple-winners
  (is (f ["4D 5S 6S 8D 3C"
          "2S 4C 7S 9H 10H"
          "3S 4S 5D 6H JH"
          "3H 4H 5C 6C JD"]
         ["3S 4S 5D 6H JH"
          "3H 4H 5C 6C JD"])))

