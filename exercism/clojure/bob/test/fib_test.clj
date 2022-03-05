(ns fib-test
  (:require [clojure.test :refer [deftest is testing]]
            [fib]))
(deftest fib-test
  
  (testing "basic"
    (is (= 1 (fib/fibo 0)))
    (is (= 1 (fib/fibo 1)))
    (is (= 2 (fib/fibo 2)))
    (is (= 3 (fib/fibo 3)))
    (is (= 5 (fib/fibo 4)))
    (is (= 8 (fib/fibo 5)))
    (is (= 13 (fib/fibo 6)))

    
    
    )
  )

