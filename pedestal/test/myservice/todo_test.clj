(ns myservice.todo-test
  (:require [clojure.test :refer :all]
            [io.pedestal.http.route :as route]
            [myservice.todo :as  todo]
            [myservice.api :refer [routes]]
            [myservice.helper :as helper]))

(deftest make-list-test
  (testing "make a todo list"
    (is (= {:name "list-name", :items {}}  (todo/make-list "list-name")))))



(comment
  
  (deftest list-create-test
    (testing "create a list"
      (let [enter-fn (:enter todo/list-create)
            context  {:request {:query-params {:name "my list"}}}]
        (is (= ""
               (enter-fn context))))))

  
  
(deftest list-view-enter
  (testing "list view enter"
    (let [enter-fn (:enter todo/list-view)
          context  {:request {:path-params {:list-id "list-id"}
                              :database {"list-id" "the list"}}
                    :url-for (route/url-for-routes routes)}]      
      (is (= "the list"
             (:result (enter-fn context))))
      ;;
      )))
  

  (deftest entity-render-test
    (testing "render entity set response"
      (let [leave-fn (:leave todo/entity-render)
            context  {:result  "some data"}]
        (is (= {:status 200, :body "some data", :headers nil}
               (:response (leave-fn context)))))
    ;;
      ))
)


;;(helper/test-request :get "/todo/my-list")