(ns raoul.contact-test
  (:require [clojure.test :refer :all]
            [raoul.contact :as contact]
            [io.pedestal.test :as test]
            [io.pedestal.interceptor :as interc]
            [io.pedestal.interceptor.chain :as chain]))

;; testing a simple function handler is easy : just invoke the function
;; adn test the returned value

(deftest respond-hello-test
  (testing "anonymous greeting"
    (let [response (contact/respond-hello {:path-params {}})]
      (is (= 200
             (:status response)))
      (is (= "hello stranger !"
             (:body response)))
      (is (= {}
             (:headers response)))))

  (testing "named greeting"
    (let [response (contact/respond-hello {:query-params {:name "bob"}})]
      (is (= 200
             (:status response)))
      (is (= "hello bob !"
             (:body response)))
      (is (= {}
             (:headers response))))))


(deftest echo-interceptor-test
  (let [enter-fn (:enter contact/echo-interceptor)]
    (testing "call echo"
      (is (= {:status 200, :body {:body    "body content"
                                  :headers {"content-type" "text/plain"}}, :headers {}}
             (:response (enter-fn {:request {:body "body content"
                                             :headers {"content-type" "text/plain"}}})))))))

(deftest interceptor-chain-test
  (let [interc-1        (interc/interceptor contact/interc-1)
        interc-2        (interc/interceptor contact/interc-2)
        interc-3        (interc/interceptor contact/interc-3)
        interc-shortcut (interc/interceptor contact/interc-shortcut)]
    (testing "reduced chain"
      (is (= {:steps-enter ["interc-1" "interc-2"],
              :steps-leave ["interc-2" "interc-1"]}
             (chain/execute {} [interc-1 interc-2]))))

    (testing "full chain"
      (is (= {:steps-enter ["interc-1" "interc-2" "interc-3"],
              :steps-leave ["interc-3" "interc-2" "interc-1"]}
             (chain/execute {} [interc-1 interc-2 interc-3]))))

    (testing "changing ionterceptor order"
      (is (= {:steps-enter ["interc-1" "interc-3" "interc-2"],
              :steps-leave ["interc-2" "interc-3" "interc-1"]}
             (chain/execute {} [interc-1 interc-3 interc-2]))))

    ;; here I as expected the shortcut interceptor to interrupt interceptor chain
    ;; so that interc-2 was not being invoked
    (testing "shortcut"
      (is (= {:steps-enter ["interc-1" "interc-3" "interc-2"],
              :steps-leave ["interc-2" "interc-3" "interc-1"]}
             (chain/execute {} [interc-1 interc-3 interc-shortcut interc-2]))))))

