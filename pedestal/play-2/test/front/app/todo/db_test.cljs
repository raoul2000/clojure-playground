(ns app.todo.db-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [app.todo.db :as t]
            [cljs.spec.alpha :as s]))


(deftest todo-crud

  (testing "create a todo"
    (let [todo (t/create-todo "title" true)]
      (is (s/valid? :todo/item todo))
      (is (= "title" (:todo/title todo)))
      (is (:todo/done todo))))

  (testing "create a todo-list"
    (let [todo-list (t/create-todo-list "list title")]
      (is (s/valid? :todo/list todo-list))
      (is (= "list title" (:todo-list/title todo-list)))
      (is (zero? (count (:todo-list/items todo-list))))))

  (testing "add todo to a todo-list"
    (let [todo-list (-> (t/create-todo-list "my list")
                        (t/add-todo-to-list (t/create-todo "todo1" false)))]
      (is (= "my list" (:todo-list/title todo-list)))
      (let [todos (:todo-list/items todo-list)]
        (is (= 1 (count todos)))
        (is (= "todo1" (:todo/title (first todos)))))))

  (testing "read todo by id"
    (let [todo-list (-> (t/create-todo-list "my list")
                        (t/add-todo-to-list {:todo/id #uuid "0-0-0-0-0"
                                             :todo/title "todo1"
                                             :todo/done false})
                        (t/add-todo-to-list {:todo/id #uuid "0-0-0-0-1"
                                             :todo/title "todo2"
                                             :todo/done false}))]
      (is (= "todo1"
             (:todo/title (t/read-todo-by-id todo-list #uuid "0-0-0-0-0"))))
      (is (= "todo2"
             (:todo/title (t/read-todo-by-id todo-list #uuid "0-0-0-0-1"))))
      (is (nil?
           (:todo/title (t/read-todo-by-id todo-list #uuid "0-0-0-0-2"))))))

  (testing "delete todo"
    (let [todo-list (-> (t/create-todo-list "my list")
                        (t/add-todo-to-list {:todo/id #uuid "0-0-0-0-0"
                                             :todo/title "todo1"
                                             :todo/done false})
                        (t/add-todo-to-list {:todo/id #uuid "0-0-0-0-1"
                                             :todo/title "todo2"
                                             :todo/done false}))
          part-list-1 (t/delete-todo todo-list #uuid "0-0-0-0-0")
          part-list-2 (t/delete-todo todo-list #uuid "0-0-0-0-1")
          part-list-3 (t/delete-todo todo-list #uuid "0-0-0-0-3")]

      (is (= 1 (count (:todo-list/items part-list-1))))
      (is (nil? (t/read-todo-by-id part-list-1 #uuid "0-0-0-0-0")))
      (is (not (nil? (t/read-todo-by-id part-list-1 #uuid "0-0-0-0-1"))))

      (is (= 1 (count (:todo-list/items part-list-2))))
      (is (nil? (t/read-todo-by-id part-list-2 #uuid "0-0-0-0-1")))
      (is (not (nil? (t/read-todo-by-id part-list-2 #uuid "0-0-0-0-0"))))

      (is (= 2 (count (:todo-list/items part-list-3))))
      (is (not (nil? (t/read-todo-by-id part-list-3 #uuid "0-0-0-0-1"))))
      (is (not (nil? (t/read-todo-by-id part-list-3 #uuid "0-0-0-0-0"))))))


  (testing "update todo"
    (let [todo-list (-> (t/create-todo-list "my list")
                        (t/add-todo-to-list {:todo/id #uuid "0-0-0-0-0"
                                             :todo/title "todo1"
                                             :todo/done false})
                        (t/add-todo-to-list {:todo/id #uuid "0-0-0-0-1"
                                             :todo/title "todo2"
                                             :todo/done false}))]
      (is (= "new title"
             (:todo/title (-> (t/update-todo todo-list #uuid "0-0-0-0-0" (t/create-todo "new title"  true))
                              (t/read-todo-by-id #uuid "0-0-0-0-0")))))
      (is (true?
           (:todo/done (-> (t/update-todo todo-list #uuid "0-0-0-0-0" (t/create-todo "new title"  true))
                           (t/read-todo-by-id #uuid "0-0-0-0-0")))))

      (is (nil? (t/update-todo todo-list #uuid "2-2-2-2-2" (t/create-todo "new title"  true))))))

  (testing "update todo title"
    (let [todo-list (-> (t/create-todo-list "my list")
                        (t/add-todo-to-list {:todo/id #uuid "0-0-0-0-0"
                                             :todo/title "todo1"
                                             :todo/done false})
                        (t/add-todo-to-list {:todo/id #uuid "0-0-0-0-1"
                                             :todo/title "todo2"
                                             :todo/done false}))]
      (is (= "new title"
             (:todo/title
              (t/read-todo-by-id
               (t/update-todo-title todo-list #uuid "0-0-0-0-0" "new title")
               #uuid "0-0-0-0-0"
               ))))))
  ;;
  )


(run-tests)