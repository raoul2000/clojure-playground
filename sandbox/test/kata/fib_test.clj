(ns kata.fib-test
  (:require [clojure.test :refer :all]
            [kata.fib :refer [fib]]))


(deftest fib-test
  (testing "when n = 0"
    (is (= 0
           (fib 0))))

  (testing "when n = 1"
    (is (= 1
           (fib 1))))

  (testing "when n = 2"
    (is (= 1
           (fib 2))))
  
  (testing "when n = 3"
    (is (= 2
           (fib 3))))
  
  (testing "when n = 4"
    (is (= 3
           (fib 4))))
  
  (testing "when n = 10"
    (is (= 55
           (fib 10))))
  
  

  ;;
  )