(ns toolbox.depend.core-test
  (:require [clojure.test :refer :all]
            [toolbox.depend.core :refer :all]))


(deftest deps->local-files-test

  (testing "basic"
    (is (= #{"local-file-2b.bash"
             "local-file-1b.bash"
             "local-file-1a.bash"
             "local-file-2a.bash"}
           (deps->local-files {:script-path "a/b/script.bash"
                               :deps         '({:ref {:match "match-1"
                                                      :line-num #{11}}
                                                :local-files ("local-file-1a.bash"
                                                              "local-file-1b.bash")}
                                               {:ref {:match "match-2"
                                                      :line-num #{2 22}}
                                                :local-files ("local-file-2a.bash"
                                                              "local-file-2b.bash")})})))))

