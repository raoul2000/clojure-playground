(ns sfsdb.read2-test
  (:require [clojure.test :refer :all]
            [clojure.string :as s]
            [sfsdb.read2 :as fsdb]
            [babashka.fs :as fs]))


(def base-path (fs/path (fs/cwd) "test/fixture/fs/root"))

(deftest meta-file?-test
  (testing "meta-file? predicate"
    (are [result path] (= result (#'fsdb/meta-file? path))
      true       (str "." fsdb/metadata-extension)
      true       (fs/path (str "." fsdb/metadata-extension))
      true       (str "some-path." fsdb/metadata-extension)
      true       (fs/path (str "some-path." fsdb/metadata-extension))
      true       (str "/folder1/folder2/." fsdb/metadata-extension)
      true       (fs/path (str "/folder1/folder2/." fsdb/metadata-extension))
      false      nil
      false      ""
      false      "/folder1/folder2/"
      false      (fs/path "/folder1/folder2/"))))

(deftest make-metadata-path-test
  (testing "create metadata path for folder"
    (is (= (fs/path base-path "folder-1" (str "." fsdb/metadata-extension))
           (#'fsdb/make-metadata-path (fs/path base-path "folder-1")))))
  (testing "create metadata path for file"
    (is (= (fs/path base-path "folder-1" "folder-1-A" (str "file.txt." fsdb/metadata-extension))
           (#'fsdb/make-metadata-path (fs/path base-path  "folder-1" "folder-1-A" "file.txt"))))))

(def read-meta #'fsdb/read-meta)

(deftest read-meta-test
  (testing "meta JSON file can be read for a folder"
    (is (= {:attribute1 "string value"}
           (read-meta  (fs/path base-path "folder-1")))))
  (testing "meta JSON file read for a file"
    (is (= {:color "green",
            :age 12,
            :sold false,
            :fruits ["apple" "orange" "banana"]}
           (read-meta  (fs/path base-path "folder-1/folder-1-A/file-1A-1.txt")))))
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
    (is (= "caught exception: JSON error (unexpected character): I"
           (read-meta (fs/path base-path "folder-2/invalid-meta-1.txt"))))))