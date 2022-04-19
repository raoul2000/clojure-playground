(ns toolbox.depend.cli-test
  (:require [clojure.test :refer :all]
            [clojure.tools.cli :refer [parse-opts]]
            [toolbox.depend.cli :as cli]))

(deftest cli-opts-test
  (testing "when minimal args, use defaults"
    (let [parsed-opts (parse-opts ["last_arg"] cli/cli-options)
          options     (:options parsed-opts)]
      (is (= "last_arg"                    ((comp first :arguments) parsed-opts)))
      (is (= cli/opt-default-pattern       (:pattern options)))
      (is (= cli/opt-default-output-format (:output-format options)))
      (is (= cli/opt-default-source-dir    (:source-dir options)))
      (is (= cli/opt-default-output-file   (:output-file options))))))