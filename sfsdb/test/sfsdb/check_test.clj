(ns sfsdb.check-test
  (:require [clojure.test :refer :all]
            [sfsdb.check :as check]
            [sfsdb.options :as opts]
            [babashka.fs :as fs]))

(def metadata-extension (:metadata-extension opts/default))

(deftest meta-file?-test
  (testing "meta-file? predicate"
    (are [result path] (= result (check/meta-file? path))
      true       (str "." metadata-extension)
      true       (fs/path (str "." metadata-extension))
      true       (str "some-path." metadata-extension)
      true       (fs/path (str "some-path." metadata-extension))
      true       (str "/folder1/folder2/." metadata-extension)
      true       (fs/path (str "/folder1/folder2/." metadata-extension))
      false      nil
      false      ""
      false      "/folder1/folder2/"
      false      (fs/path "/folder1/folder2/"))))

(deftest in-db?-test
  (testing "test predicate"
    (are [pred db-path] (pred (check/in-db? db-path))
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

(deftest relative-db-path-test
  (testing "test relative db-path"
    (are [pred db-path] (pred (check/relative-db-path? db-path))
      true?  "."
      true?  "./"
      true?  "./a/b"
      true?  "./a/b/../../../c" ;; true but outside db

      false?  ""
      false?  ".."
      false?  "a"
      false?  "a/b")))

(deftest writable-dir?-test
  (testing "test predicate"
    (are [pred path] (pred (check/writable-dir? path))
      false?  ""
      false?  "."
      false?  (fs/path (fs/cwd) "not_found")
      true?   (fs/cwd)
      true?   (fs/path (fs/cwd) "test")
      true?   (str (fs/path (fs/cwd) "test")))))