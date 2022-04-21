(ns toolbox.log.time-distrib.save-test
  (:require [clojure.test :refer :all]
            [toolbox.log.time-distrib.save :as sav]))

(def date-1 (java.time.LocalDateTime/of 2022 04 21 12 20 11))
(def date-2 (java.time.LocalDateTime/of 2022 04 21 13 30 10))

(deftest timestamp->string-test
  (testing "when timestamp is LocalDateTime"
    (is (= ["2022-04-21T12:20:11" '("event value")]
           (sav/timestamp->string [date-1 "event value"]))))

  (testing "when timestamp is a string"
    (is (= ["string" '("event value")]
           (sav/timestamp->string ["string" "event value"])))))

(deftest events->json-test
  (testing "when several events"
    (is (= "{\"file\":\"file.txt\",\"results\":[[\"2022-04-21T12:20:11\",[\"event 1\"]],[\"2022-04-21T13:30:10\",[\"event 2\"]]]}"
           (sav/events->json {:file     "file.txt"
                              :results [[date-1 "event 1"]
                                        [date-2 "event 2"]]}))))
  (testing "when no event"
    (is (= "{\"file\":\"file.txt\",\"results\":[]}"
           (sav/events->json {:file     "file.txt"
                              :results []})))))