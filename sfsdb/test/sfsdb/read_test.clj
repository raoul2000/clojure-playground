(ns sfsdb.read-test
  (:require [clojure.test :refer :all]
            [clojure.string :as s]
            [sfsdb.read :as fsdb]
            [sfsdb.options :as opts]
            [babashka.fs :as fs]))

(def metadata-extension (:metadata-extension opts/default))

(def options {:with-meta?    true
              :with-content? true
              :root-path     (fs/path (fs/path (fs/cwd) "test/fixture/fs/root"))})

(def base-path (fs/path (fs/cwd) "test/fixture/fs/root"))
(def read-meta #'fsdb/read-meta)


(deftest make-metadata-path-test
  (testing "create metadata path for folder"
    (is (= (fs/path base-path "folder-1" (str "." metadata-extension))
           (#'fsdb/make-metadata-path (fs/path base-path  "folder-1")))))
  (testing "create metadata path for file"
    (is (= (fs/path base-path "folder-1" "folder-1-A" (str "file.txt." metadata-extension))
           (#'fsdb/make-metadata-path (fs/path base-path  "folder-1" "folder-1-A" "file.txt"))))))


(deftest read-meta-test
  (testing "meta JSON file can be read for a folder"
    (is (= {:attribute1 "string value"}
           (read-meta  (fs/path base-path "folder-1")))))
  (testing "meta JSON file read for a file"
    (is (= {:color "green",
            :age 12,
            :sold false,
            :fruits ["apple" "orange" "banana"]}
           (read-meta  (fs/path base-path  "folder-1/folder-1-A/file-1A-1.txt")))))
  (testing "when no meta for folder, returns nil"
    (is (= nil
           (read-meta  (fs/path base-path "folder-2")))))
  (testing "when no meta for file, returns nil"
    (is (= nil
           (read-meta  (fs/path base-path "folder-1/folder-1-A/file-1A-2.txt")))))
  (testing "when folder not found returns nil"
    (is (= nil
           (read-meta  (fs/path base-path "folder-NOT_FOUNF")))))
  (testing "when file not found, returns nil"
    (is (= nil
           (read-meta  (fs/path base-path "folder-1/folder-1-A/NOT_FOUND.txt")))))
  (testing "when JSON meta file parse fails, returns error message"
    (is (s/starts-with?
         (:error (read-meta (fs/path base-path "folder-2/invalid-meta-1.txt")))
         "failed to read metadata file"))))


(deftest parent-of-test
  (testing "returns parent path "
    (are [parent db-path] (= parent (#'fsdb/parent-of db-path))
      nil  ""
      nil   "a"
      "a"   "a/b"
      "a/b" "a/b/c"
      "a/b" "a/b/file.txt"))
  (testing "throws when db-path is nil"
    (is (thrown? Exception (#'fsdb/parent-of nil)))))


(deftest select-ancerstors-test
  (testing "when no parent"
    (is (= []
           (fsdb/select-ancestors "folder-1" (constantly true) options)))
    (is (= []
           (fsdb/select-ancestors "" (constantly true) options))))

  (testing "when parent found with no filter"
    (is (= [(fsdb/read-db-path "folder-1" options)]
           (fsdb/select-ancestors "folder-1/folder-1-A"
                                  (constantly true)
                                  options)))

    (is (= [(fsdb/read-db-path "folder-1/folder-1-A" options)
            (fsdb/read-db-path "folder-1" options)]
           (fsdb/select-ancestors "folder-1/folder-1-A/file-1A-1.txt"
                                  (constantly true)
                                  options)))

    (is (= [(fsdb/read-db-path "folder-1/folder-1-A" options)
            (fsdb/read-db-path "folder-1" options)]
           (fsdb/select-ancestors "folder-1/folder-1-A/folder-1-A-blue"
                                  (constantly true)
                                  options))))

  (testing "when parent found with filter"
    (is (= [(fsdb/read-db-path "folder-1/folder-1-A" options)]
           (fsdb/select-ancestors "folder-1/folder-1-A/folder-1-A-blue"
                                  #(= "long folder name" (get-in % [:meta :fullname]))
                                  options)))
    (is (= []
           (fsdb/select-ancestors "folder-1/folder-1-A/folder-1-A-blue"
                                  (constantly false)
                                  options))))

  (testing "when select only first"
    (is (= [(fsdb/read-db-path "folder-1/folder-1-A" options)]
           (fsdb/select-ancestors "folder-1/folder-1-A/folder-1-A-blue"
                                  (constantly true)
                                  (assoc options :find-first? true))))

    (is (= [(fsdb/read-db-path "folder-1/folder-1-A" options)
            (fsdb/read-db-path "folder-1" options)]
           (fsdb/select-ancestors "folder-1/folder-1-A/folder-1-A-blue"
                                  (constantly true)
                                  (assoc options :find-first? false))))))


(deftest walk-and-select-test
  (testing "when items are selected"
    (is (= [{:name ".gitkeep",           :dir? false, :path "folder-2/.gitkeep"}
            {:name "invalid-meta-1.txt", :dir? false, :path "folder-2/invalid-meta-1.txt"}]
           (#'fsdb/walk-and-select (fs/path base-path "folder-2")
                                   (constantly true)
                                   {:root-path base-path})))

    (is (= [{:name ".gitkeep",       :dir? false, :path "folder-1/folder-1-A/.gitkeep"}
            {:name "file-1A-1.txt",  :dir? false, :path "folder-1/folder-1-A/file-1A-1.txt"}
            {:name "file-1A-2.txt",  :dir? false, :path "folder-1/folder-1-A/file-1A-2.txt"}
            {:name "folder-1-A-blue",:dir? true,  :path "folder-1/folder-1-A/folder-1-A-blue"}
            {:name ".gitkeep",       :dir? false, :path "folder-1/folder-1-A/folder-1-A-blue/.gitkeep"}]
           (#'fsdb/walk-and-select (fs/path base-path "folder-1/folder-1-A")
                                   (constantly true)
                                   {:root-path base-path})))

    (is (= [{:name "file-1B-1.txt"   :dir? false  :path "folder-1/folder-1-B/file-1B-1.txt"}]
           (#'fsdb/walk-and-select (fs/path base-path "folder-1/folder-1-B")
                                   #(= (:name %) "file-1B-1.txt")
                                   {:root-path base-path}))))

  (testing "when no item is selected"
    (is (empty? (#'fsdb/walk-and-select (fs/path base-path "folder-1")
                                        (constantly false)
                                        {:root-path base-path}))))

  (testing "throws when dir-path does not exist"
    (is (thrown? Exception (#'fsdb/walk-and-select (fs/path base-path "not_found")
                                                   (constantly false)
                                                   {:root-path base-path}))))

  (testing "return empty seq when dir-path refers to a file"
    (is (empty? (#'fsdb/walk-and-select (fs/path base-path "folder-2/invalid-meta-1.txt")
                                        (constantly false)
                                        {:root-path base-path})))))





(deftest select-descendants-test
  (testing "when descendants are found"
    (is (seq (fsdb/select-descendants "folder-1"
                                      (constantly true)
                                      {:root-path base-path})))

    (is (seq (fsdb/select-descendants ""
                                      (constantly true)
                                      {:root-path base-path}))))

  (testing "when not descendants are found returns empty seq"
    (is (empty? (fsdb/select-descendants "folder-1"
                                         (constantly false)
                                         {:root-path base-path}))))

  (testing "returns nil when db-path is not found"
    (is (nil? (fsdb/select-descendants "not_found" identity {}))))

  (testing "throws when db-path is nil"
    (is (thrown? Exception
                 (fsdb/select-descendants nil identity {}))))

  (testing "throws when selected? is not a function"
    (is (thrown? AssertionError
                 (fsdb/select-descendants "folder-1" true {}))))

  (testing "throws  when db-path is outside db-root"
    (is (thrown? Exception
                 (fsdb/select-descendants ".." identity {}))))

  (testing "when selector predicate uses metadata"
    (is (= [{:name "file-1A-1.txt",
             :dir? false,
             :path "folder-1/folder-1-A/file-1A-1.txt",
             :meta
             {:color "green", :age 12, :sold false, :fruits ["apple" "orange" "banana"]}}]
           (fsdb/select-descendants ""
                                    #(= "green" (get-in % [:meta :color]))
                                    {:root-path base-path
                                     :with-meta? true}))))

  (testing "when selector predicate uses content"
    (is (= 5
           (count (fsdb/select-descendants ""
                                           #(and (not (:dir? %))
                                                 (s/starts-with? (:content %) "Occaecat"))
                                           {:root-path     base-path
                                            :with-meta?    false
                                            :with-content? true}))))))

(def base-path-2 (fs/path (fs/cwd) "test/fixture/fs/root2"))

(deftest read-db-path-test
  (testing "reading file object with no extra"
    (is (= {:name "file1.txt", :dir? false, :path "file1.txt"}
           (fsdb/read-db-path "file1.txt" {:with-meta?    false
                                           :with-content? false
                                           :root-path     base-path-2}))))

  (testing "read file object with metadata"
    (is (= {:name "file1.txt", :dir? false, :path "file1.txt" :meta nil}
           (fsdb/read-db-path "file1.txt" {:with-meta?    true
                                           :with-content? false
                                           :root-path     base-path-2}))
        "return :meta nil when no metadata exists for file")

    (is (= {:name "file2.txt", :dir? false, :path "file2.txt" :meta {:name "file2.txt"}}
           (fsdb/read-db-path "file2.txt" {:with-meta?    true
                                           :with-content? false
                                           :root-path     base-path-2}))
        "read and parse json metadata when found"))

  (testing "read file object with metadata and content"
    (is (= {:name "file1.txt", :dir? false, :path "file1.txt" :meta nil :content "some text content"}
           (fsdb/read-db-path "file1.txt" {:with-meta?    true
                                           :with-content? true
                                           :root-path     base-path-2})))
    (is (= {:name "file2.txt", :dir? false, :path "file2.txt" :meta {:name "file2.txt"} :content "file2.txt content"}
           (fsdb/read-db-path "file2.txt" {:with-meta?    true
                                           :with-content? true
                                           :root-path     base-path-2}))))

  (testing "read file object with content"
    (is (= {:name "file1.txt", :dir? false, :path "file1.txt" :content "some text content"}
           (fsdb/read-db-path "file1.txt" {:with-meta?    false
                                           :with-content? true
                                           :root-path     base-path-2})))
    (is (= {:name "file3.txt", :dir? false, :path "file3.txt", :content ""}
           (fsdb/read-db-path "file3.txt" {:with-meta?    false
                                           :with-content? true
                                           :root-path     base-path-2}))
        "returns :content \"\" of empty files"))

  (testing "reading folder object with no extra"
    (is (= {:name "dir1", :dir? true, :path "dir1"}
           (fsdb/read-db-path "dir1" {:with-meta?    false
                                      :with-content? false
                                      :root-path     base-path-2})))
    (testing "reading folder object with meta"
      (is (= {:name "dir1", :dir? true, :path "dir1" :meta {:prop "str value"}}
             (fsdb/read-db-path "dir1" {:with-meta?    true
                                        :with-content? false
                                        :root-path     base-path-2})))

      (is (= {:name "dir2", :dir? true, :path "dir2", :meta nil}
             (fsdb/read-db-path "dir2" {:with-meta?    true
                                        :with-content? false
                                        :root-path     base-path-2}))
          "return :meta nil when no metadata exists for folder"))

    (testing "read folder object with content"
      (is (= {:name "dir1", :dir? true, :path "dir1", :content '({:name "f11.txt", :dir? false, :path "dir1/f11.txt"}
                                                                 {:name "f12.txt", :dir? false, :path "dir1/f12.txt"})}
             (fsdb/read-db-path "dir1" {:with-meta?    false
                                        :with-content? true
                                        :root-path     base-path-2})))
      (is (= {:name "dir3", :dir? true, :path "dir3", :content '()}
             (fsdb/read-db-path "dir3" {:with-meta?    false
                                        :with-content? true
                                        :root-path     base-path-2}))
          "returns an empty seq when folder has no content"))

    #_(testing "when base path is invalid"
        (is (thrown?
             (fsdb/read-db-path "dir3" {:with-meta?    false
                                        :with-content? true
                                        :root-path     "/not_found"}))))))