(ns sfsdb.export-test
  (:require [clojure.test :refer :all]
            [sfsdb.export :as exp]
            [babashka.fs :as fs]))

(def base-path (fs/path (fs/cwd) "test/fixture/fs/root2"))
(def export-path (fs/path (fs/cwd) "test/fixture/export"))

(defn prepare-for-export [dest-path]
  (let [exported-fs-path (fs/path export-path dest-path)]
    (when (fs/exists? exported-fs-path)
      (fs/delete exported-fs-path))
    exported-fs-path))

(deftest export-test
  (testing "it exports a file given its db-path to a folder"
    (let [exported-fs-path (prepare-for-export "f11.txt")]
      (exp/export "dir1/f11.txt" export-path {:root-path base-path})
      (is (fs/exists? exported-fs-path))))
  
  (testing "export a folder given its db-path to another folder"
         )
  )