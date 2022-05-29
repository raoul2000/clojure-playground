(ns response-test
  (:require [clojure.test :refer :all]
            [response :as resp]))

(deftest response-test
  (testing "when it returns a ok response"
    (is (= {:status 200, :body "body cpontent", :headers {}}
           (resp/ok "body cpontent")))))