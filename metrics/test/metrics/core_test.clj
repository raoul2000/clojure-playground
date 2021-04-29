(ns metrics.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [metrics.core]
            [metrics.parse]))

(deftest str->int-test
  (testing "string to int"
    (is (= 1    (metrics.core/str->int "1")))
    (is (= 0    (metrics.core/str->int "00")))
    (is (= -42  (metrics.core/str->int "-42")))
    (is (= 1234 (metrics.core/str->int "001234")) "ignore 0 prefix")
    (is (= 1234 (metrics.core/str->int " 1234 ")) "trim before convert")

    (is (thrown? NumberFormatException (metrics.core/str->int "")))
    (is (thrown? NumberFormatException (metrics.core/str->int "ab")))
    (is (thrown? NumberFormatException (metrics.core/str->int "1.2")))))

(def serie1 {0 1 2 2})
(def serie2 {1 1 2 5})
(def serie3 {0 1 2 5})

(deftest complete-serie
  (testing "completing missing keys"
    (is (= {0 1 1 0 2 2} (metrics.core/complete-serie serie1)))
    (is (= {0 0 1 1 2 5} (metrics.core/complete-serie serie2)))
    (is (= {0 1 1 0 2 5} (metrics.core/complete-serie serie3)))))

(def map-seq1 [{:date "date-1" :task-id "task-id-1" :latestDownloadCount 1 :execCount 11}
               {:date "date-2" :task-id "task-id-1" :latestDownloadCount 23 :execCount 11}
               {:date "date-3" :task-id "task-id-2" :latestDownloadCount 2 :execCount 22}])

(def map-result1 {"task-id-1" [{:task-id "task-id-1" :latestDownloadCount 1}
                               {:task-id "task-id-1" :latestDownloadCount 23}]
                  "task-id-2" [{:task-id "task-id-2" :latestDownloadCount 2}]})

(deftest distrib-download-count
  (testing "distrib-download-count"
    (is (= map-result1 (metrics.core/distrib-download-count map-seq1)))))


