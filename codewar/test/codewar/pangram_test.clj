(ns codewar.pangram-test
  (:require [clojure.test :refer [deftest is ]]
            [codewar.pangram :refer [pangram?]]))

(deftest Tests
  (is (= (pangram? "The quick brown fox jumps over the lazy dog.") true))
  (is (= (pangram? "Pack my box with five dozen liquor jugs.") true))
  (is (= (pangram? "Not every sentence is a a pangram. An example") false)))