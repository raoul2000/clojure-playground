(ns sfsdb.read-test
  (:require [clojure.test :refer :all]
            [sfsdb.read :as fs]
            [babashka.fs :as bfs]))

(def base-path (bfs/path (bfs/cwd) "test/fixture/fs"))
(defn make-path-string [& p]
  (str (apply bfs/path p)))


(deftest normalize-path-test
  (testing "Normalizing path"
    (are [result path] (= result (#'fs/normalize-path path))
      "/aa/bb/cc" "/aa/bb/cc"
      "aa/bb/cc"  "aa/bb/cc"
      "aa/bb/cc"  "aa\\bb\\cc"
      "aa/bb/cc"  "./aa/bb/cc"
      "aa/bb/cc"  ".\\aa\\bb\\cc"
      "c:/aa/bb"  "c:\\aa\\bb")))

(deftest split-paths-test
  (testing "split a path in folder and file names vector"
    (are [result args] (= result (#'fs/split-paths args))
      ["c:" "aaa" "bbb"]  (make-path-string "c:" "aaa" "bbb")
      ["111" "222" "333"] (make-path-string  "111" "222" "333"))))

(deftest path-seq-test
  (testing "returns all path"
    (is (= '("root"
             "root/folder-1"
             "root/folder-1/folder-1-A"
             "root/folder-1/folder-1-B"
             "root/folder-2")
           (#'fs/path-seq (bfs/path base-path)))))

  (testing "throws when path does not exists"
    (is (thrown? Exception (#'fs/path-seq "some/path")))))