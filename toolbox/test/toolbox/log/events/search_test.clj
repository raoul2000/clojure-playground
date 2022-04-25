(ns toolbox.log.events.search-test
  (:require [clojure.test :refer :all]
            [toolbox.log.events.search :as search]))

(def date-time-formatter (java.time.format.DateTimeFormatter/ofPattern "yyyy MM dd HH mm ss nnn"))

(deftest parse-line-timestamp-test
  (testing "when parse success returns DateTime"
    (let [date-time (search/parse-line-timestamp "2022-04-11 21:56:14,161 qsdqsd")]
      (is (instance? java.time.LocalDateTime date-time))
      (is (= "2022 04 11 21 56 14 161" (.format date-time date-time-formatter)))))

  (testing "when parse fails returns nil"
    (let [date-time (search/parse-line-timestamp "NO MATCH")]
      (is (nil? date-time)))))


(deftest parse-line-event-test
  (testing "when match, conj event to old value and returns"
    (is (= [["TS" "abcd event efg"]]
           (search/parse-line-event [] "TS" "abcd event efg" #".*event.*")))
    (is (= [["TS" "abcd event efg" "event"]]
           (search/parse-line-event [] "TS" "abcd event efg" #".*(event).*"))))

  (testing "when match, return unchanged old value"
    (is (= []
           (search/parse-line-event [] "TS" "abcd event efg" #".*XXX.*")))))

