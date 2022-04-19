(ns toolbox.depend.save-test
  (:require [clojure.test :refer :all]
            [toolbox.depend.save :refer :all]))

(def dep-a {:script-path "a/b/script-a.bash"
            :deps         '({:ref {:match "match-1"
                                   :line-num #{11}}
                             :local-files ("local-file-1a.bash"
                                           "local-file-1b.bash")}
                            {:ref {:match "match-2"
                                   :line-num #{2 22}}
                             :local-files ("local-file-2a.bash"
                                           "local-file-2b.bash")})})

(def dep-b {:script-path "a/b/script-b.bash"
            :deps         '({:ref {:match "match-1"
                                   :line-num #{11}}
                             :local-files ("local-file-1a.bash"
                                           "local-file-1b.bash")}
                            {:ref {:match "match-2"
                                   :line-num #{2 22}}
                             :local-files ("local-file-2a.bash"
                                           "local-file-2b.bash")})})

(def dep-a-bis {:script-path "a/b/script-a.bash"
                :deps         '({:ref {:match "match-1"
                                       :line-num #{11}}
                                 :local-files ("local-file-1a.bash")})})

(def dep-c {:script-path "a/b/script-c.bash"
            :deps         '()})

(deftest tgf-node-definition-test
  (testing "when single node"
    (is (= #{"1527713512 a/b/script-a.bash"}
           (tgf-node-definition [dep-a])))
    (is (= #{"1584971814 a/b/script-c.bash"}
           (tgf-node-definition [dep-c]))))

  (testing "when multi node"
    (is (=  #{"1527713512 a/b/script-a.bash" "1556342663 a/b/script-b.bash"}
            (tgf-node-definition [dep-a dep-b]))))

  (testing "when duplicate nodes"
    (is (= #{"1527713512 a/b/script-a.bash"}
           (tgf-node-definition [dep-a dep-a dep-a])))
    (is (= #{"1527713512 a/b/script-a.bash"}
           (tgf-node-definition [dep-a dep-a-bis])))))


(deftest tgf-edge-definition-test
  (testing "when single node with 4 edges"
    (is (= #{"1527713512 550187907"
             "1527713512 1437691588"
             "1527713512 578817058"
             "1527713512 1466320739"}
           (tgf-edge-definition [dep-a]))))

  (testing "when single node with no edge"
    (is (= #{}
         (tgf-edge-definition [dep-c]))))

  (testing "when multi node"
    (is (= #{"1556342663 578817058"
             "1556342663 1466320739"
             "1527713512 550187907"
             "1527713512 1437691588"
             "1556342663 550187907"
             "1527713512 578817058"
             "1527713512 1466320739"
             "1556342663 1437691588"}
           (tgf-edge-definition [dep-a dep-b])))

    (is (= #{"1556342663 578817058"
             "1556342663 1466320739"
             "1556342663 550187907"
             "1556342663 1437691588"}
           (tgf-edge-definition [dep-c dep-b]))))
  (testing "when duplicate edges"
    (is
     (= #{"1527713512 550187907"
          "1527713512 1437691588"
          "1527713512 578817058"
          "1527713512 1466320739"}
      (tgf-edge-definition [dep-a dep-a])))))

(deftest format-tgf-test
  (testing "when create tgf string"
    (is (= "1527713512 a/b/script-a.bash\n#\n1527713512 550187907\n1527713512 1437691588\n1527713512 578817058\n1527713512 1466320739"
           (format-tgf [dep-a])))))
