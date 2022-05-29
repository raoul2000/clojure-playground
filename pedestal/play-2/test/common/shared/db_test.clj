(ns shared.db-test
  (:require [clojure.test :refer :all]
            [shared.db :as db]
            [clojure.spec.alpha :as s]))


(deftest todo-crud

  (testing "create a todo"
    (let [todo (db/create-todo "title" true)]
      (is (s/valid? :todo/item todo))
      (is (= "title" (:todo/title todo)))
      (is (:todo/done todo))))

  (testing "create a todo-list"
    (let [todo-list (db/create-todo-list "list title")]
      (is (s/valid? :todo/list todo-list))
      (is (= "list title" (:todo-list/title todo-list)))
      (is (zero? (count (:todo-list/items todo-list))))))

  (testing "add todo to a todo-list"
    (let [todo-list (-> (db/create-todo-list "my list")
                        (db/add-todo-to-list (db/create-todo "todo1" false)))]
      (is (= "my list" (:todo-list/title todo-list)))
      (let [todos (:todo-list/items todo-list)]
        (is (= 1 (count todos)))
        (is (= "todo1" (:todo/title (first todos)))))))

  (testing "read todo by id"
    (let [todo-list (-> (db/create-todo-list "my list")
                        (db/add-todo-to-list {:todo/id #uuid "0-0-0-0-0"
                                             :todo/title "todo1"
                                             :todo/done false})
                        (db/add-todo-to-list {:todo/id #uuid "0-0-0-0-1"
                                             :todo/title "todo2"
                                             :todo/done false}))]
      (is (= "todo1"
             (:todo/title (db/read-todo-by-id todo-list #uuid "0-0-0-0-0"))))
      (is (= "todo2"
             (:todo/title (db/read-todo-by-id todo-list #uuid "0-0-0-0-1"))))
      (is (nil?
           (:todo/title (db/read-todo-by-id todo-list #uuid "0-0-0-0-2"))))))

  (testing "delete todo"
    (let [todo-list (-> (db/create-todo-list "my list")
                        (db/add-todo-to-list {:todo/id #uuid "0-0-0-0-0"
                                             :todo/title "todo1"
                                             :todo/done false})
                        (db/add-todo-to-list {:todo/id #uuid "0-0-0-0-1"
                                             :todo/title "todo2"
                                             :todo/done false}))
          part-list-1 (db/delete-todo todo-list #uuid "0-0-0-0-0")
          part-list-2 (db/delete-todo todo-list #uuid "0-0-0-0-1")
          part-list-3 (db/delete-todo todo-list #uuid "0-0-0-0-3")]

      (is (= 1 (count (:todo-list/items part-list-1))))
      (is (nil? (db/read-todo-by-id part-list-1 #uuid "0-0-0-0-0")))
      (is (not (nil? (db/read-todo-by-id part-list-1 #uuid "0-0-0-0-1"))))

      (is (= 1 (count (:todo-list/items part-list-2))))
      (is (nil? (db/read-todo-by-id part-list-2 #uuid "0-0-0-0-1")))
      (is (not (nil? (db/read-todo-by-id part-list-2 #uuid "0-0-0-0-0"))))

      (is (= 2 (count (:todo-list/items part-list-3))))
      (is (not (nil? (db/read-todo-by-id part-list-3 #uuid "0-0-0-0-1"))))
      (is (not (nil? (db/read-todo-by-id part-list-3 #uuid "0-0-0-0-0"))))))


  (testing "update todo"
    (let [todo-list (-> (db/create-todo-list "my list")
                        (db/add-todo-to-list {:todo/id #uuid "0-0-0-0-0"
                                             :todo/title "todo1"
                                             :todo/done false})
                        (db/add-todo-to-list {:todo/id #uuid "0-0-0-0-1"
                                             :todo/title "todo2"
                                             :todo/done false}))]
      (is (= "new title"
             (:todo/title (-> (db/update-todo todo-list #uuid "0-0-0-0-0" (db/create-todo "new title"  true))
                              (db/read-todo-by-id #uuid "0-0-0-0-0")))))
      (is (true?
           (:todo/done (-> (db/update-todo todo-list #uuid "0-0-0-0-0" (db/create-todo "new title"  true))
                           (db/read-todo-by-id #uuid "0-0-0-0-0")))))

      (is (nil? (db/update-todo todo-list #uuid "2-2-2-2-2" (db/create-todo "new title"  true))))))

  (testing "update todo title"
    (let [todo-list (-> (db/create-todo-list "my list")
                        (db/add-todo-to-list {:todo/id #uuid "0-0-0-0-0"
                                             :todo/title "todo1"
                                             :todo/done false})
                        (db/add-todo-to-list {:todo/id #uuid "0-0-0-0-1"
                                             :todo/title "todo2"
                                             :todo/done false}))]
      (is (= "new title"
             (:todo/title
              (db/read-todo-by-id
               (db/update-todo-title todo-list #uuid "0-0-0-0-0" "new title")
               #uuid "0-0-0-0-0"
               ))))))
  ;;
  )


;;(run-tests)