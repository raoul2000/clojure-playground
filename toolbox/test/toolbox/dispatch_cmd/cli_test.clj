(ns toolbox.dispatch-cmd.cli-test
  (:require [clojure.test :refer :all]
            [toolbox.dispatch-cmd.cli :as cli]))

(deftest  valid-port?-test
  (testing "valid-port?"
    (is (true?  (cli/valid-port? "22")))
    (is (true?  (cli/valid-port? "1")))
    (is (true?  (cli/valid-port? "001")))
    (is (false? (cli/valid-port? "0")))
    (is (false? (cli/valid-port? "-1")))
    (is (false? (cli/valid-port? 22)))))

(deftest parse-connexion-string-test
  (testing "parse minimum connexion string"
    (is (= {:label    "username@host",
            :host     "host",
            :port     nil,
            :username "username",
            :password nil,
            :value    "username@host"}

           (cli/parse-connexion-string "username@host"))))

  (testing "with password"
    (is (= {:label "username@host",
            :host "host",
            :port nil,
            :username "username",
            :password "pwd",
            :value "pwd:username@host"}

           (cli/parse-connexion-string "pwd:username@host"))))

  (testing "with port number"
    (is (= {:label "username@host",
            :host "host",
            :port  22,
            :username "username",
            :password nil,
            :value "username@host:22"}

           (cli/parse-connexion-string "username@host:22"))))

  (testing "with password and port"
    (is (= {:label "username@host",
            :host "host",
            :port 22,
            :username "username",
            :password "pwd",
            :value "pwd:username@host:22"}

           (cli/parse-connexion-string "pwd:username@host:22"))))

  (testing "when username contains char '@'"
    (is (= {:label "name1@name2@host",
            :host "host",
            :port nil,
            :username "name1@name2",
            :password nil,
            :value "name1@name2@host"}

           (cli/parse-connexion-string "name1@name2@host"))))

  (testing "when pport is invalid"
    (let [port-is-string (cli/parse-connexion-string "name@host:xxx")
          port-is-neg    (cli/parse-connexion-string "name@host:-10")
          port-is-empty  (cli/parse-connexion-string "name@host:")]
      (is (nil? (:port port-is-string)))
      (is (nil? (:port port-is-neg)))
      (is (nil? (:port port-is-empty)))))

  (testing "when password is invalid"
    (let [pwd-is-empty (cli/parse-connexion-string ":name@host")]
      (is (nil? (:password pwd-is-empty))))))

(deftest apply-default-test
  (testing "applying default password"
    (is (= '({:label "1", :password "pwd"}
             {:label "2", :password "password"}
             {:label "3", :password "pwd"})

           (cli/apply-default [{:label "1" :password nil}
                               {:label "2" :password "password"}
                               {:label "3" :password nil}] "pwd" nil))))

  (testing "applying nil default password does not change input"
    (is (= '({:label "1", :password nil, :port 22}
             {:label "2", :password "password", :port 22}
             {:label "3", :password nil, :port 22})

           (cli/apply-default [{:label "1" :password nil}
                               {:label "2" :password "password"}
                               {:label "3" :password nil}] nil 22))))

  (testing "applying default port"
    (is (= '({:label "1", :port 22}
             {:label "2", :port 21}
             {:label "3", :port 23})

           (cli/apply-default [{:label "1", :port 22}
                               {:label "2", :port nil}
                               {:label "3", :port 23}] nil 21))))

  (testing "applying nil port does not change input"
    (is (= '({:label "1", :port 22}
             {:label "2", :port nil}
             {:label "3", :port 23})

           (cli/apply-default [{:label "1", :port 22}
                               {:label "2", :port nil}
                               {:label "3", :port 23}] nil nil))))

  (testing "applying default password and port"
    (is (= '({:label "1", :port 22  :password "pwd"}
             {:label "2", :port 888 :password "pwd2"}
             {:label "3", :port 22  :password "def-pwd"}
             {:label "4", :port 888 :password "def-pwd"})

           (cli/apply-default [{:label "1", :port 22  :password "pwd"}
                               {:label "2", :password "pwd2"}
                               {:label "3", :port 22}
                               {:label "4"}] "def-pwd" 888)))))