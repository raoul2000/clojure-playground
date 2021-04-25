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
    (is (= {:a 1}    (metrics.core/add-entry {} [:a 1]    {:b str})))
    (is (= {:a "42"} (metrics.core/add-entry {} [:a "42"] {:b str})))
    (is (= {:a nil}  (metrics.core/add-entry {} [:a]      {:b str})))))

(def conv-identity {:a identity :b identity})
(def conv-int      {:a identity :b metrics.core/str->int})

(deftest mapify-line
  (testing "success"
    (is (= {:a "a", :b "b"} (metrics.core/mapify-line "a,b" conv-identity)))
    (is (= {:a "ab"}        (metrics.core/mapify-line "ab"  conv-identity)))
    (is (= {:a ""}          (metrics.core/mapify-line ""    conv-identity))))

  (testing "int conversion"
    (is (= {:a "a", :b 1}   (metrics.core/mapify-line "a,1" conv-int)))
    (is (= {:a "ab"}        (metrics.core/mapify-line "ab"  conv-int)))
    (is (= {:a ""}          (metrics.core/mapify-line ""    conv-int))))

  (testing "conversion fails"
    (is (thrown? NumberFormatException (metrics.core/mapify-line "a,b"   conv-int)))))

(def serie1 {0 1 2 2})
(def serie2 {1 1 2 5})
(def serie3 {0 1 2 5})

(deftest complete-serie
  (testing "completing missing keys"
    (is (= {0 1 1 0 2 2} (metrics.core/complete-serie serie1)))
    (is (= {0 0 1 1 2 5} (metrics.core/complete-serie serie2)))
    (is (= {0 1 1 0 2 5} (metrics.core/complete-serie serie3)))))

(deftest parse-str
  (testing "success"
    (is (= '({:a "id1" :b 2} {:a "id2" :b 3})
           (metrics.core/parse-str "id1,2\nid2,3" conv-int))))

  (testing "invalid input"
    (is (= '({:a ""})
           (metrics.core/parse-str "" conv-int)))))


