(ns toolbox.depend.file-match-test
  (:require [clojure.test :refer :all]
            [toolbox.depend.file-match :refer :all]))

(deftest split-path-by-slash-test
  (testing "when one separator"
    (is (= '("abc" "def")
           (split-path-by-slash "abc/def")))

    (is (= '("abc")
           (split-path-by-slash "abc/")))

    (is (= '("def")
           (split-path-by-slash "/def")))

    (is (empty?
         (split-path-by-slash "/"))))

  (testing "when multi separator"
    (is (= '("abc" "def" "ghi")
           (split-path-by-slash "abc/def/ghi")))

    (is (= '("abc"  "ghi")
           (split-path-by-slash "abc//ghi")))

    (is (= '("ghi")
           (split-path-by-slash "//ghi")))

    (is (empty?
         (split-path-by-slash "//"))))

  (testing "when no separator"
    (is (= '("abc")
           (split-path-by-slash "abc")))

    (is (empty?
         (split-path-by-slash "")))))

(deftest common-seg-test
  (testing "when last match"
    (is (= '("a" "b" "c.txt")
           (common-seg "/a/b/c.txt" "/a/b/c.txt")))

    (is (= '("b" "c.txt")
           (common-seg "/a/b/c.txt" "/_/b/c.txt")))

    (is (= '("c.txt")
           (common-seg "/a/b/c.txt" "/_/_/c.txt"))))


  (testing "when last does not match"
    (is (empty?
         (common-seg "/a/b/c.txt" "/a/b/_.txt")))

    (is (empty?
         (common-seg "/a/b/c.txt" "/_/b/_.txt")))

    (is (empty?
         (common-seg "/a/b/c.txt" "/_/_/_.txt"))))

  (testing "subject longer than match"
    (is (= '("a" "b" "c.txt")
           (common-seg "/_/_/a/b/c.txt" "/a/b/c.txt")))))

(deftest path-match-score-test
  (testing "when distinct match"
    (is (= {1 '("a/c.txt" "/a/b/c.txt" "c.txt")}
           (path-match-score "c.txt" ["c.txt" "/a/b/c.txt" "a/c.txt" "d.txt"])))

    (is (= {1 '("/a/b/c.txt" "c.txt")
            2 '("a/c.txt")}
           (path-match-score "a/c.txt" ["c.txt" "/a/b/c.txt" "a/c.txt" "d.txt"]))))

  (testing "when no match"
    (is (empty?
         (path-match-score "c.txt" ["d.txt" "a/b/d.txt"]))))

  (testing "when no candidate"
    (is (nil?
         (path-match-score "c.txt" [])))))

(deftest best-path-match-test
  (testing "when found single match"
    (is (= '("a/b/c.txt")
           (best-path-match "c.txt" ["a/b/c.txt" "a/b/_.txt" "_.txt"])))
    (is (= '("c.txt")
           (best-path-match "c.txt" ["c.txt"]))))

  (testing "when found multi match"
    (is (= '("a/b/c.txt" "x/b/c.txt")
           (best-path-match "c.txt" ["x/b/c.txt" "a/b/c.txt" "a/b/_.txt" "_.txt"]))))

  (testing "when no match found"
    (is (nil?
         (best-path-match "a/c.txt" ["a/1.txt" "a/b/2.txt" "a/b/_.txt" "_.txt"])))))