(ns sfsdb.read2-test
  (:require [clojure.test :refer :all]
            [sfsdb.read2 :as fsdb]
            [babashka.fs :as fs]))


(def base-path (fs/path (fs/cwd) "test/fixture/fs/root"))

(deftest meta-file?-test
  (testing "meta-file? predicate"
    (are [result path] (= result (#'fsdb/meta-file? path))
      nil        nil
      false      ""
      true       (str "some-path." fsdb/metadata-extension)
      true       (fs/path (str "some-path." fsdb/metadata-extension))
      true       (str "/folder1/folder2/." fsdb/metadata-extension)
      true       (fs/path (str "/folder1/folder2/." fsdb/metadata-extension))
      false      "/folder1/folder2/"
      false      (fs/path "/folder1/folder2/"))))

(deftest make-metadata-path-test
  (testing "create metadata path for folder"
    (is (= (fs/path base-path "folder-1" (str "." fsdb/metadata-extension))
           (#'fsdb/make-metadata-path (fs/path base-path "folder-1")))))
  (testing "create metadata path for file"
    (is (= (fs/path base-path "folder-1" "folder-1-A" (str "file.txt." fsdb/metadata-extension))
           (#'fsdb/make-metadata-path (fs/path base-path  "folder-1" "folder-1-A" "file.txt"))))))
