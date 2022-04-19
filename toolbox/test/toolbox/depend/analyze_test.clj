(ns toolbox.depend.analyze-test
  (:require [clojure.test :refer :all]
            [toolbox.depend.analyze :refer :all]))

(def reducer-acc  {:line-count 0
                   :result     []})

(deftest match-reducer-test
  (testing "when no match"
    (is (= {:line-count 1, :result []}
           (re-match-reducer reducer-acc "not match")))
    (is (= {:line-count 1, :result []}
           (re-match-reducer reducer-acc ""))))

  (testing "when simple match"
    (is (= {:line-count 1, :result [{:line-num 1, :match '("script.bash")}]}
           (re-match-reducer reducer-acc "script.bash")))

    (is (= {:line-count 1, :result [{:line-num 1, :match '("$HOME/folder/script.bash")}]}
           (re-match-reducer reducer-acc "$HOME/folder/script.bash")))

    (is (= {:line-count 1, :result [{:line-num 1, :match '(".bash_profile")}]}
           (re-match-reducer reducer-acc ". .bash_profile")))

    (is (= {:line-count 1, :result [{:line-num 1, :match '("/folder/folder/log.bash.src")}]}
           (re-match-reducer reducer-acc ". /folder/folder/log.bash.src"))))

  (testing "ignore commented line"
    (is (= {:line-count 1, :result []}
           (re-match-reducer reducer-acc "# $HOME/folder/script.bash")))

    (is (= {:line-count 1, :result []}
           (re-match-reducer reducer-acc "    # $HOME/folder/script.bash")))

    (is (= {:line-count 1, :result []}
           (re-match-reducer reducer-acc "\t\t\t# $HOME/folder/script.bash"))))

  (testing "when several scripts per line"
    (is (= {:line-count 1, :result  [{:line-num 1, :match '("script-1.bash" "script2.bash")}]}
           (re-match-reducer reducer-acc "script-1.bash ; script2.bash")))))


(deftest index-by-script-test
  (testing "index one item"
    (is (= {"a.bash" #{1}}
           (index-by-script [{:line-num 1 :match '("a.bash")}]))))

  (testing "when one script per line"
    (is (= {"a.bash" #{1}, "b.bash" #{3}, "c.bash" #{5}}
           (index-by-script [{:line-num 1 :match '("a.bash")}
                             {:line-num 3 :match '("b.bash")}
                             {:line-num 5 :match '("c.bash")}]))))

  (testing "when same script on distinct lines"
    (is (= {"a.bash" #{1 2}, "c.bash" #{6 5}}
           (index-by-script [{:line-num 1 :match '("a.bash")}
                             {:line-num 2 :match '("a.bash")}
                             {:line-num 5 :match '("c.bash")}
                             {:line-num 6 :match '("c.bash")}]))))

  (testing "when distinct script on same lines"
    (is (= {"a.bash" #{1}, "b.bash" #{1}, "c.bash" #{2}, "d.bash" #{2}}
           (index-by-script [{:line-num 1 :match '("a.bash")}
                             {:line-num 1 :match '("b.bash")}
                             {:line-num 2 :match '("c.bash")}
                             {:line-num 2 :match '("d.bash")}]))))

  (testing "when same script on same lines"
    (is (= {"a.bash" #{1}, "b.bash" #{2}}
           (index-by-script [{:line-num 1 :match '("a.bash")}
                             {:line-num 1 :match '("a.bash")}
                             {:line-num 2 :match '("b.bash")}
                             {:line-num 2 :match '("b.bash")}])))))

