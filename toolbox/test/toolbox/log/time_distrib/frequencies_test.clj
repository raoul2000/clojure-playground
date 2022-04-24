(ns toolbox.log.time-distrib.frequencies-test
  (:require [clojure.test :refer :all]
            [toolbox.log.time-distrib.frequencies :as freq]))


(deftest valid-time-unit?-test
  (testing "when valid time unit"
    (is (freq/valid-time-unit? :day))
    (is (freq/valid-time-unit? :hour))
    (is (freq/valid-time-unit? :minute))
    (is (freq/valid-time-unit? :second))
    (is (freq/valid-time-unit? :millis)))

  (testing "when not valid time unit"
    (is (nil? (freq/valid-time-unit? :not-valid)))))


(deftest round-timestamp-fn-test
  (testing "always returns a function"
    (is (function? (freq/round-timestamp-fn :day)))
    (is (function? (freq/round-timestamp-fn :hour)))
    (is (function? (freq/round-timestamp-fn :minute)))
    (is (function? (freq/round-timestamp-fn :second)))
    (is (function? (freq/round-timestamp-fn :millis))))

  (testing "when chrono unit is valid"
    (let [date-1 (java.time.LocalDateTime/of 2022 04 21 11 20 11 123456789)]
      (is (= "2022-04-21T00:00"
             (.toString ((freq/round-timestamp-fn :day)    date-1))))
      (is (= "2022-04-21T11:00"
             (.toString ((freq/round-timestamp-fn :hour)   date-1))))
      (is (= "2022-04-21T11:20"
             (.toString ((freq/round-timestamp-fn :minute) date-1))))
      (is (= "2022-04-21T11:20:11"
             (.toString ((freq/round-timestamp-fn :second) date-1))))
      (is (= "2022-04-21T11:20:11.123"
             (.toString ((freq/round-timestamp-fn :millis) date-1))))))

  (testing "when chrono unit is not valid"
    (is (= identity
           (freq/round-timestamp-fn :invalid)))))

(deftest timestamp-coll-mapper-test
  (testing "when not empty results"
    (is (= '("TS1" "TS2")
           (freq/timestamp-coll-mapper {:file "file1.txt"
                                        :results [["TS1" "event-line"]
                                                  ["TS2" "event-line"]]}))))
  (testing "when empty results"
    (is (= '()
           (freq/timestamp-coll-mapper {:file "file1.txt"
                                        :results []})))))

(deftest occurency-count-test
  (testing "when success"
    (is (= ["TS1" 3]
           (freq/occurency-count ["TS1" [:a :b :c]])))
    (is (= ["TS1" 0]
           (freq/occurency-count ["TS1" []])))
    (is (= ["TS1" 0]
           (freq/occurency-count ["TS1"])))))