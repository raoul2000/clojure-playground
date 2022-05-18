(ns myservice.api-test
  (:require [clojure.test :refer :all]
            [myservice.api :as api]))

(deftest accepted-type-test
  (testing "accepted type"
    (is (= "text/plain"
           (api/accepted-type {:request {:accept {:field "text/plain"}}})))
    (is (= "text/html"
           (api/accepted-type {:request {:accept {:field "text/html"}}})))
    (is (= "text/plain"
           (api/accepted-type {:request {}})))
    (is (= "application/xml"
           (api/accepted-type {:request {:accept {:field "application/xml"}}})))))


(deftest transform-content-test
  (testing "transform to string"
    (is (= "body content"
           (api/transform-content "body content" "text/plain")))
    (is (= 42
           (api/transform-content 42 "text/plain")))
    (is (= {:key "value"}
           (api/transform-content {:key "value"} "text/plain"))))
  
  (testing "transform to json"
    (is (= "\"body content\""
           (api/transform-content "body content" "application/json")))
    (is (= "{\"key\":\"value\"}"
           (api/transform-content {:key "value"} "application/json")))))

(deftest coerce-to-test
  (testing "coerce response"
    (is (= {:body "{\"name\":\"bob\"}",
            :headers {"Content-Type" "application/json"}}
           (api/coerce-to {:body {:name "bob"}} "application/json")))))

(deftest no-content-type?-test
  (testing "predicate no-content-type?"
    (is (= false
           (api/no-content-type? {:response {:headers {"Content-Type" "text/plain"}}})))
    (is (= true
           (api/no-content-type? {:response {}})))))