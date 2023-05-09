(ns sfsdb.read2-test
  (:require [clojure.test :refer :all]
            [clojure.string :as s]
            [sfsdb.read2 :as fsdb]
            [babashka.fs :as fs]))


(def options {:with-meta? true
              :with-content? true
              :root-path (fs/path (fs/path (fs/cwd) "test/fixture/fs/root"))})

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




(deftest in-db?-test
  (testing "test predicate"
    (are [pred db-path] (pred (#'fsdb/in-db? db-path))
      true?  ""
      true?  "a"
      true?  "a/b"
      true?  "a/b/c"
      true?  "a/../b"
      true?  "a/../b/../c"
      true?  "./b"
      true?  "./b/../c"

      false?  ".."
      false?  "a/../../a"
      false?  "a/../../b"
      false?  "./../a"
      false?  "a/b/../../.."
      false?  "/a/b"
      false?  "/../a")))



(deftest path->db-path-test
  (testing "Converts file system path to db path"
    (when (fs/windows?)
      (is (= ""
             (#'fsdb/path->db-path "c:\\folder1" "c:\\folder1")))
      (is (= ""
             (#'fsdb/path->db-path "c:\\folder1" "c:\\folder1\\")))
      (is (= "folder2"
             (#'fsdb/path->db-path "c:\\folder1" "c:\\folder1\\folder2")))
      (is (= "folder2/folder3"
             (#'fsdb/path->db-path "c:\\folder1" "c:\\folder1\\folder2\\folder3")))

      (is (thrown? AssertionError  (#'fsdb/path->db-path "c:\\folder1" "c:\\"))
          "throws when path is not in db")
      (is (thrown? AssertionError  (#'fsdb/path->db-path "folder1" "c:\\folder1"))
          "throws when root-path is not relative")
      (is (thrown? AssertionError  (#'fsdb/path->db-path "c:\\folder1" "c:\\folder1\\.."))
          "throws when path is not in db"))))



(deftest parent-of-test
  (testing "returns parent path "
    (are [parent db-path] (= parent (#'fsdb/parent-of db-path))
      nil   "a"
      "a"   "a/b"
      "a/b" "a/b/c"
      "a/b" "a/b/file.txt")))




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

