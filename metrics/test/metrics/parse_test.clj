(ns metrics.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [metrics.core]
            [metrics.parse]))

(deftest add-entry
  (testing "add entry with conv"
    (is (= {:a 1}    (metrics.parse/add-entry {}       [:a 1]   {:a identity})))
    (is (= {:a "42"} (metrics.parse/add-entry {}       [:a 42]  {:a str})))
    (is (= {:a "42"} (metrics.parse/add-entry {}       [:a 42]  {:a str})))
    (is (= {:a nil}  (metrics.parse/add-entry {}       [:a]     {:a identity})))
    (is (= {:a ""}   (metrics.parse/add-entry {}       [:a]     {:a str})))
    (is (= {:a "42"} (metrics.parse/add-entry {:a "x"} [:a 42]  {:a str}))))

  (testing "add entry with conv not found"
    (is (= {:a 1}    (metrics.parse/add-entry {} [:a 1]    {:b str})))
    (is (= {:a "42"} (metrics.parse/add-entry {} [:a "42"] {:b str})))
    (is (= {:a nil}  (metrics.parse/add-entry {} [:a]      {:b str})))))

(def conv-identity {:a identity :b identity})
(def conv-int      {:a identity :b metrics.core/str->int})

(deftest mapify-line
  (testing "success"
    (is (= {:a "a", :b "b"} (metrics.parse/mapify-line "a,b" conv-identity)))
    (is (= {:a "ab"}        (metrics.parse/mapify-line "ab"  conv-identity)))
    (is (= {:a ""}          (metrics.parse/mapify-line ""    conv-identity))))

  (testing "int conversion"
    (is (= {:a "a", :b 1}   (metrics.parse/mapify-line "a,1" conv-int)))
    (is (= {:a "ab"}        (metrics.parse/mapify-line "ab"  conv-int)))
    (is (= {:a ""}          (metrics.parse/mapify-line ""    conv-int))))

  (testing "conversion fails"
    (is (thrown? NumberFormatException (metrics.parse/mapify-line "a,b"   conv-int)))))

(def serie1 {0 1 2 2})
(def serie2 {1 1 2 5})
(def serie3 {0 1 2 5})

(deftest parse-str
  (testing "success"
    (is (= '({:a "id1" :b 2} {:a "id2" :b 3})
           (metrics.parse/parse-str "id1,2\nid2,3" conv-int))))

  (testing "invalid input"
    (is (= '({:a ""})
           (metrics.parse/parse-str "" conv-int)))))


