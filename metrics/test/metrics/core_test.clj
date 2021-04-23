(ns metrics.core-test
  (:require [clojure.test :refer [deftest is are testing]]
            [metrics.core]))

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

(deftest add-entry
  (testing "add entry with conv"
    (is (= {:a 1}    (metrics.core/add-entry {}       [:a 1]   {:a identity})))
    (is (= {:a "42"} (metrics.core/add-entry {}       [:a 42]  {:a str})))
    (is (= {:a "42"} (metrics.core/add-entry {}       [:a 42]  {:a str})))
    (is (= {:a nil}  (metrics.core/add-entry {}       [:a]     {:a identity})))
    (is (= {:a ""}   (metrics.core/add-entry {}       [:a]     {:a str})))
    (is (= {:a "42"} (metrics.core/add-entry {:a "x"} [:a 42]  {:a str}))))

  (testing "add entry with conv not found"
    (is (= {:a 1}    (metrics.core/add-entry {} [:a 1]  {:b str})))
    (is (= {:a 42}   (metrics.core/add-entry {} [:a 42] {:b str})))
    (is (= {:a nil}  (metrics.core/add-entry {} [:a]    {:b str})))))

(def conv-identity {:a identity :b identity})

(deftest mapify-line
  (testing "success"
    (is (= {:a "a", :b "b"} (metrics.core/mapify-line "a,b" conv-identity)))
    (is (= {:a "ab"}        (metrics.core/mapify-line "ab"  conv-identity)))
    (is (= {:a ""}          (metrics.core/mapify-line ""    conv-identity)))))

(deftest test-2
  (testing "are"
    (are [x y] (= x y)
      1 (metrics.core/str->int "1"))))
