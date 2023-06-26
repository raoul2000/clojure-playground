(ns sfsdb.health.core-test
  (:require [clojure.test :refer :all]
            [sfsdb.health.core :as c]))


(deftest meta-file-for-data-file?-test
  (testing "assert on file"
    (are [result path] (= result (#'c/meta-file-for-data-file? path))
      true "file.meta"
      true "/a/b/c/file.meta"
      false "file.txt"
      false "/a/b/c/file.txt"
      false ".meta"
      false "/a/b/c/.meta")))

(deftest run-single-exam-test
  (testing "Runs a simple single exam"
    (let [run-exam (c/run-single-exam "subject")]
      (is (= {:exam-id [{:subject "subject", :result 1}]}
             (run-exam {} [:exam-id {:selected? (constantly true)
                                     :examine   (constantly 1)}]))
          "returns the exam result")

      (is (= {:exam-id [{:subject "subject", :result ["result"]}]}
             (run-exam {} [:exam-id {:selected? (constantly true)
                                     :examine   (constantly ["result"])}]))
          "returns the exam result")))

  (testing "Runs no exam when subject is not selected"
    (let [run-exam (c/run-single-exam "subject")]
      (is (= {}
             (run-exam {} [:exam-id {:selected? (constantly false)
                                     :examine   (constantly 1)}]))
          "returns nothing")))

  (testing "Accumulates results with conj when no accumulator is provided"
    (let [run-exam (c/run-single-exam "subject")]
      (is (= {:exam-id [{:subject "previous", :result 2}
                        {:subject "subject", :result 1}]}
             (run-exam {:exam-id [{:subject "previous" :result 2}]}
                       [:exam-id {:selected? (constantly true)
                                  :examine   (constantly 1)}]))
          "conj new result")))

  (testing "Don't accumulates results when exam is not selected"
    (let [run-exam (c/run-single-exam "subject")]
      (is (= {:exam-id [{:subject "previous", :result 2}]}
             (run-exam {:exam-id [{:subject "previous" :result 2}]}
                       [:exam-id {:selected? (constantly false)
                                  :examine   (constantly 1)}]))
          "conj new result")))

  (testing "Acculmulate results with provided accumulator function"
    (let [run-exam (c/run-single-exam "subject")]
      (is (= {:exam-id [{:subject "previous", :result 2}]}
             (run-exam {:exam-id [{:subject "previous" :result 2}]}
                       [:exam-id {:selected? (constantly true)
                                  :examine   (constantly {:success false})
                                  :accumulator (fn [old val]
                                                 (cond-> old
                                                   (-> val :result :success) (conj  val)))}]))
          "don't add result when not success")

      (is (= {:exam-id 15}
             (run-exam {:exam-id  10}
                       [:exam-id {:selected? (constantly true)
                                  :examine   (constantly 5)
                                  :accumulator (fn [old val]
                                                 (+ old (-> val :result)))}]))
          "addition result accumulation"))))

