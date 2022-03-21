(ns myservice.todo-test
  (:require [clojure.test :refer :all]
            [myservice.todo :as  todo]))

(deftest make-list-test
  (testing "make a todo list"
    (is (= {:name "list-name", :items {}}  (todo/make-list "list-name")))))


(deftest list-view-enter
  (testing "list view enter"
    (let [enter-fn (:enter todo/list-view)
          context  {:request {:path-params {:list-id "list-id"}
                              :database {"list-id" "the list"}}}]

      (is (= {:request
              {:path-params {:list-id "list-id"}, :database {"list-id" "the list"}},
              :result "the list"}
             (enter-fn context))))))