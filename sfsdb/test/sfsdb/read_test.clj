(ns sfsdb.read-test
  (:require [clojure.test :refer :all]
            [clojure.string :as s]
            [sfsdb.read :as fsdb]
            [sfsdb.options :as opts]
            [babashka.fs :as fs]))

(def metadata-extension (:metadata-extension opts/default))

(def base-path (fs/path (fs/cwd) "test/fixture/fs/root"))
(def read-meta #'fsdb/read-meta)

;; ----------------------------------------------------------------------------------------------------


(deftest make-metadata-path-test
  (testing "create metadata path for folder"
    (is (= (fs/path base-path "folder-1" (str "." metadata-extension))
           (#'fsdb/make-metadata-path (fs/path base-path  "folder-1")))))
  (testing "create metadata path for file"
    (is (= (fs/path base-path "folder-1" "folder-1-A" (str "file.txt." metadata-extension))
           (#'fsdb/make-metadata-path (fs/path base-path  "folder-1" "folder-1-A" "file.txt"))))))


;; ----------------------------------------------------------------------------------------------------


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
    (try
      (read-meta (fs/path base-path "folder-2/invalid-meta-1.txt"))
      (is (= 1 2) "should throw")
      (catch Exception ex
        (let [error-data (ex-data ex)]
          (is (s/starts-with? (.getMessage ex) "failed to read metadata file"))
          (is (s/ends-with? (:path error-data) "invalid-meta-1.txt.meta"))
          (is (=  "JSON error (unexpected character): I" (:cause error-data))))))))


;; ----------------------------------------------------------------------------------------------------


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