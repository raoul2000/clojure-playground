(ns toolbox.log.events.cli-test
  (:require [clojure.test :refer :all]
            [toolbox.log.events.cli :as cli]))

(deftest  string->re-test
  (testing "when re valid"
    (is (= "{:re #\".*\\.bash.*\"}"
           (.toString (cli/string->re ".*\\.bash.*")))))

  (testing "when re invalid"
    (is (= "{:error \"Invalid regular expression : Dangling meta character '*' near index 0\\r\\n*\\r\\n^\"}"
           (.toString (cli/string->re "*"))))

    (is (= "{:error \"Invalid regular expression : \"}"
           (.toString (cli/string->re nil))))))