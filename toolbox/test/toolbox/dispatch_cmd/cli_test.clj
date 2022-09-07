(ns toolbox.dispatch-cmd.cli-test
  (:require [clojure.test :refer :all]
            [toolbox.dispatch-cmd.cli :as cli]))

(deftest  valid-port?-test
  (testing "valid-port?"
    (is (true? (cli/valid-port? "22")))
    (is (true? (cli/valid-port? "1")))
    (is (true? (cli/valid-port? "001")))
    (is (false? (cli/valid-port? "0")))
    (is (false? (cli/valid-port? "-1")))
    (is (false? (cli/valid-port? 22)))))

(deftest parse-connexion-string-test
  (testing "parsing complete connexion string"
    (is (= {:label "username@host",
            :host "host",
            :port "22",
            :username "username",
            :password "pwd",
            :value "pwd:username@host:22"}
           (cli/parse-connexion-string "pwd:username@host:22"))))

  (testing "when port is missing or invalid"
    
    (is (= {:label "username@host",
            :host nil ,
            :port nil,
            :username "username",
            :password "pwd",
            :value "pwd:username@host"}
           (cli/parse-connexion-string "pwd:username@host:port")) "port is not an int string")
    
    (is (= {:label "username@host",
            :host "host",
            :port nil,
            :username "username",
            :password "pwd",
            :value "pwd:username@host"}
           (cli/parse-connexion-string "pwd:username@host")) "port is missing")
    
    (is (= {:label "username@host",
            :host "host",
            :port nil,
            :username "username",
            :password "pwd",
            :value "pwd:username@host"}
           (cli/parse-connexion-string "pwd:username@host:")) "colon but not value for port"))
  (testing "when password is missing"
    (is (= {:label "username@host",
            :host "host",
            :port "22",
            :username "username",
            :password nil,
            :value "username@host:22"}
           (cli/parse-connexion-string "username@host:22")) "password is missing")

    (is (= {:label ":username@host",
            :host "host",
            :port "22",
            :username ":username",
            :password nil,
            :value ":username@host:22"}
           (cli/parse-connexion-string ":username@host:22")) "colon but not password"))
  (testing "missing pieces"
    (is (= {:label "pwd:@host",
            :host "host",
            :port "22",
            :username "pwd:",
            :password nil,
            :value "pwd:@host:22"}
           (cli/parse-connexion-string "pwd:@host:22")) "missing username")))
