(ns toolbox.log.time-distrib.save-test
  (:require [clojure.test :refer :all]
            [toolbox.log.time-distrib.save :as sav]))

(def date-1 (java.time.LocalDateTime/of 2022 04 21 12 20 11))
(def date-2 (java.time.LocalDateTime/of 2022 04 21 13 30 10))

(deftest timestamp->string-test
  (testing "when timestamp is LocalDateTime"
    (is (= ["2022-04-21T12:20:11" "event value"]
           (sav/timestamp->string [date-1 "event value"]))))

  (testing "when timestamp is a string"
    (is (= ["string" "event value"]
           (sav/timestamp->string ["string" "event value"])))))

(deftest prepare-for-json-test
  (testing "when several events"
    (is (= [{:file      "file.txt",
             :results  '(["2022-04-21T12:20:11" "event 1"]
                         ["2022-04-21T13:30:10" "event 2"])}]
           (sav/prepare-for-json [{:file     "file.txt"
                                   :results [[date-1 "event 1"]
                                             [date-2 "event 2"]]}]))))

  (testing "when several events with capturing groups"
    (is (= [{:file      "file.txt",
             :results  '(["2022-04-21T12:20:11" "event 1" "capture 1" "capture 2"]
                         ["2022-04-21T13:30:10" "event 2" "capture 3" "capture 4"])}]
           (sav/prepare-for-json [{:file     "file.txt"
                                   :results [[date-1 "event 1" "capture 1" "capture 2"]
                                             [date-2 "event 2" "capture 3" "capture 4"]]}]))))

  (testing "when one event"
    (is (= [{:file     "file.txt",
             :results '(["2022-04-21T13:30:10" "line" "capture-1"])}]
           (sav/prepare-for-json [{:file     "file.txt"
                                   :results [[date-2 "line" "capture-1"]]}]))))

  (testing "when no event"
    (is (= [{:file    "file.txt",
             :results '()}]
           (sav/prepare-for-json [{:file     "file.txt"
                                   :results []}])))))

(deftest prepare-for-csv-test
  (testing "when several events"
    (is (= '(["file.txt" "date-1" "event 1"]
             ["file.txt" "date-2" "event 2"])
           (sav/prepare-for-csv [{:file     "file.txt"
                                  :results [["date-1" "event 1"]
                                            ["date-2" "event 2"]]}])))))