(ns myservice.response-test
  (:require [clojure.test :refer :all]
            [myservice.response :as resp]))


(deftest create-response-test
  (testing "create a simple response with no header"
    (let  [create-response #'myservice.response/create-response]
      (is (= {:status 200, :body "ok", :headers nil}
             (create-response 200 "ok")))))

  (testing "create a simple response with custom headersr"
    (let  [create-response #'myservice.response/create-response]
      (is (= {:status 200, :body "ok", :headers {"X-Header" "X-value"}}
             (create-response 200 "ok" "X-Header" "X-value"))))))

(deftest ok-response-test
  (testing "ok response"
    (is (= {:status 200, :body "body", :headers nil}
           (resp/ok "body"))))
  (testing "ok response with custom headers"
    (is (= {:status 200, :body "body", :headers {"X-Header" "X-value"}}
           (resp/ok "body" "X-Header" "X-value")))))