(ns sfsdb.select-test
  (:require [clojure.test :refer :all]
            [clojure.string :as s]
            [sfsdb.read :as fsdb]
            [sfsdb.select :as db-select]
            [sfsdb.options :as opts]
            [babashka.fs :as fs]))

(def options {:with-meta?    true
              :with-content? true
              :root-path     (fs/path (fs/path (fs/cwd) "test/fixture/fs/root"))})

(def base-path (fs/path (fs/cwd) "test/fixture/fs/root"))

;; ----------------------------------------------------------------------------------------------------


(deftest parent-of-test
  (testing "returns parent path "
    (are [parent db-path] (= parent (#'db-select/parent-of db-path))
      nil  ""
      nil   "a"
      "a"   "a/b"
      "a/b" "a/b/c"
      "a/b" "a/b/file.txt"))
  (testing "throws when db-path is nil"
    (is (thrown? Exception (#'db-select/parent-of nil)))))


;; ----------------------------------------------------------------------------------------------------


(deftest walk-and-select-test
  (testing "when items are selected"
    (is (= [{:name ".gitkeep",           :dir? false, :path "folder-2/.gitkeep"}
            {:name "invalid-meta-1.txt", :dir? false, :path "folder-2/invalid-meta-1.txt"}]
           (#'db-select/walk-and-select (fs/path base-path "folder-2")
                                   (constantly true)
                                   {:root-path base-path})))

    (is (= [{:name ".gitkeep",       :dir? false, :path "folder-1/folder-1-A/.gitkeep"}
            {:name "file-1A-1.txt",  :dir? false, :path "folder-1/folder-1-A/file-1A-1.txt"}
            {:name "file-1A-2.txt",  :dir? false, :path "folder-1/folder-1-A/file-1A-2.txt"}
            {:name "folder-1-A-blue",:dir? true,  :path "folder-1/folder-1-A/folder-1-A-blue"}
            {:name ".gitkeep",       :dir? false, :path "folder-1/folder-1-A/folder-1-A-blue/.gitkeep"}]
           (#'db-select/walk-and-select (fs/path base-path "folder-1/folder-1-A")
                                   (constantly true)
                                   {:root-path base-path})))

    (is (= [{:name "file-1B-1.txt"   :dir? false  :path "folder-1/folder-1-B/file-1B-1.txt"}]
           (#'db-select/walk-and-select (fs/path base-path "folder-1/folder-1-B")
                                   #(= (:name %) "file-1B-1.txt")
                                   {:root-path base-path}))))

  (testing "when no item is selected"
    (is (empty? (#'db-select/walk-and-select (fs/path base-path "folder-1")
                                        (constantly false)
                                        {:root-path base-path}))))

  (testing "throws when dir-path does not exist"
    (is (thrown? Exception (#'db-select/walk-and-select (fs/path base-path "not_found")
                                                   (constantly false)
                                                   {:root-path base-path}))))

  (testing "return empty seq when dir-path refers to a file"
    (is (empty? (#'db-select/walk-and-select (fs/path base-path "folder-2/invalid-meta-1.txt")
                                        (constantly false)
                                        {:root-path base-path})))))

;; ----------------------------------------------------------------------------------------------------


(deftest select-ancerstors-test
  (testing "when no parent"
    (is (= []
           (db-select/select-ancestors "folder-1" (constantly true) options)))
    (is (= []
           (db-select/select-ancestors "" (constantly true) options))))

  (testing "when parent found with no filter"
    (is (= [(fsdb/read-db-path "folder-1" options)]
           (db-select/select-ancestors "folder-1/folder-1-A"
                                  (constantly true)
                                  options)))

    (is (= [(fsdb/read-db-path "folder-1/folder-1-A" options)
            (fsdb/read-db-path "folder-1" options)]
           (db-select/select-ancestors "folder-1/folder-1-A/file-1A-1.txt"
                                  (constantly true)
                                  options)))

    (is (= [(fsdb/read-db-path "folder-1/folder-1-A" options)
            (fsdb/read-db-path "folder-1" options)]
           (db-select/select-ancestors "folder-1/folder-1-A/folder-1-A-blue"
                                  (constantly true)
                                  options))))

  (testing "when parent found with filter"
    (is (= [(fsdb/read-db-path "folder-1/folder-1-A" options)]
           (db-select/select-ancestors "folder-1/folder-1-A/folder-1-A-blue"
                                  #(= "long folder name" (get-in % [:meta :fullname]))
                                  options)))
    (is (= []
           (db-select/select-ancestors "folder-1/folder-1-A/folder-1-A-blue"
                                  (constantly false)
                                  options))))

  (testing "when select only first"
    (is (= [(fsdb/read-db-path "folder-1/folder-1-A" options)]
           (db-select/select-ancestors "folder-1/folder-1-A/folder-1-A-blue"
                                  (constantly true)
                                  (assoc options :find-first? true))))

    (is (= [(fsdb/read-db-path "folder-1/folder-1-A" options)
            (fsdb/read-db-path "folder-1" options)]
           (db-select/select-ancestors "folder-1/folder-1-A/folder-1-A-blue"
                                  (constantly true)
                                  (assoc options :find-first? false))))))


;; ----------------------------------------------------------------------------------------------------

(deftest select-descendants-test
  (testing "when descendants are found"
    (is (seq (db-select/select-descendants "folder-1"
                                      (constantly true)
                                      {:root-path base-path})))

    (is (seq (db-select/select-descendants ""
                                      (constantly true)
                                      {:root-path base-path}))))

  (testing "when not descendants are found returns empty seq"
    (is (empty? (db-select/select-descendants "folder-1"
                                         (constantly false)
                                         {:root-path base-path}))))

  (testing "returns nil when db-path is not found"
    (is (nil? (db-select/select-descendants "not_found" identity {}))))

  (testing "throws when db-path is nil"
    (is (thrown? Exception
                 (db-select/select-descendants nil identity {}))))

  (testing "throws when selected? is not a function"
    (is (thrown? AssertionError
                 (db-select/select-descendants "folder-1" true {}))))

  (testing "throws  when db-path is outside db-root"
    (is (thrown? Exception
                 (db-select/select-descendants ".." identity {}))))

  (testing "when selector predicate uses metadata"
    (is (= [{:name "file-1A-1.txt",
             :dir? false,
             :path "folder-1/folder-1-A/file-1A-1.txt",
             :meta
             {:color "green", :age 12, :sold false, :fruits ["apple" "orange" "banana"]}}]
           (db-select/select-descendants "folder-1"
                                    #(= "green" (get-in % [:meta :color]))
                                    {:root-path base-path
                                     :with-meta? true}))))

  (testing "when selector predicate uses content"
    (is (= 5
           (count (db-select/select-descendants ""
                                           #(and (not (:dir? %))
                                                 (s/starts-with? (:content %) "Occaecat"))
                                           {:root-path     base-path
                                            :with-meta?    false
                                            :with-content? true}))))))