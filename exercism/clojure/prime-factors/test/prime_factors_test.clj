(ns prime-factors-test
  (:require [clojure.test :refer [deftest is]]
            prime-factors))

(deftest prime-number?-test
  (is (not (prime-factors/prime-number? 1)))
  (is (not (prime-factors/prime-number? 4)))
  (is  (prime-factors/prime-number? 2))
  
  
  )

(deftest one
  (is (= [] (prime-factors/of 1))))

(deftest two
  (is (= [2] (prime-factors/of 2))))

(deftest three
  (is (= [3] (prime-factors/of 3))))

(deftest four
  (is (= [2, 2] (prime-factors/of 4))))

(deftest six
  (is (= [2, 3] (prime-factors/of 6))))

(deftest eight
  (is (= [2, 2, 2] (prime-factors/of 8))))

(deftest nine
  (is (= [3, 3] (prime-factors/of 9))))

(deftest twenty-seven
  (is (= [3, 3, 3] (prime-factors/of 27))))

(deftest six-hundred-twenty-five
  (is (= [5, 5, 5, 5] (prime-factors/of 625))))

(deftest a-large-number
  (is (= [5, 17, 23, 461] (prime-factors/of 901255))))

(deftest a-huge-number
  (is (= [11, 9539, 894119] (prime-factors/of 93819012551))))
