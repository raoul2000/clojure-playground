(ns sfsdb.health-test
  (:require [clojure.test :refer :all]
            [clojure.string :as s]
            [sfsdb.health :as h]
            [sfsdb.options :as opts]
            [babashka.fs :as fs]))

(deftest apply-exam-on-test
  (testing "create a mapper function for exams"
    (is (= [{:ex1 "item"}]
           (map (#'h/apply-exam-on "item") {:ex1 {:can-pass-exam? (constantly true)
                                                  :pass-exam?     (constantly false)}})))

    (is (= [nil]
           (map (#'h/apply-exam-on "item") {:ex1 {:can-pass-exam? (constantly false)
                                                  :pass-exam?     (constantly false)}})))))

(deftest diagnose-item-test
  (testing "apply exams when all fails"
    (is (= [[{:ex1 "item1"} {:ex2 "item1"}] [{:ex1 "item2"} {:ex2 "item2"}]]
           (map (#'h/diagnose-item {:ex1 {:can-pass-exam? (constantly true)
                                          :pass-exam?     (constantly false)}
                                    :ex2 {:can-pass-exam? (constantly true)
                                          :pass-exam?     (constantly false)}})
                ["item1"  "item2"]))))

  (testing "remove empty result when item can't pass exam"
    (is (= [[] []]
           (map (#'h/diagnose-item {:ex1 {:can-pass-exam? (constantly false)
                                          :pass-exam?     (constantly true)}
                                    :ex2 {:can-pass-exam? (constantly false)
                                          :pass-exam?     (constantly false)}})
                ["item1"  "item2"])))))

(deftest exams-result-reducer-test
  (testing "creates a reducer function for exams results"
    (is (= {:ex1 ["1" "2" "4"], :ex2 ["3" "3" "5"]}
           (reduce #'h/exams-results {} [{:ex1 "1"}
                                         {:ex1 "2"}
                                         {:ex2 "3"}
                                         {:ex2 "3"}
                                         {:ex1 "4"}
                                         {:ex2 "5"}])))))