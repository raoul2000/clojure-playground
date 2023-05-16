(ns sfsdb.convert-test
  (:require [clojure.test :refer :all]
            [sfsdb.convert :as convert]
            [babashka.fs :as fs]))

(deftest fs-path->db-path-test
  (testing "Converts file system path to db path"
    (when (fs/windows?)
      (is (= ""
             (convert/fs-path->db-path "c:\\folder1" "c:\\folder1")))
      (is (= ""
             (convert/fs-path->db-path "c:\\folder1" "c:\\folder1\\")))
      (is (= "folder2"
             (convert/fs-path->db-path "c:\\folder1" "c:\\folder1\\folder2")))
      (is (= "folder2/folder3"
             (convert/fs-path->db-path "c:\\folder1" "c:\\folder1\\folder2\\folder3")))

      (is (thrown? AssertionError  (convert/fs-path->db-path "c:\\folder1" "c:\\"))
          "throws when path is not in db")
      (is (thrown? AssertionError  (convert/fs-path->db-path "folder1" "c:\\folder1"))
          "throws when root-path is not absolute")
      (is (thrown? AssertionError  (convert/fs-path->db-path "c:\\folder1" "c:\\folder1\\.."))
          "throws when path is not in db"))))