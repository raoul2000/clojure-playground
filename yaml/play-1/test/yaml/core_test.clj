(ns yaml.core-test
  (:require [clojure.test :refer :all]
            [clj-yaml.core :as yaml]))

(deftest a-test
  (testing "parsing a simple data string"
    (is (= {:name "bob"} 
           (yaml/parse-string "name : bob")))
    (is (= {:name "bob marley"}
           (yaml/parse-string "name : 'bob marley'")))
    )
  
  (testing "multi line string"
    (is (= {}
           (yaml/parse-string "name: \"\"\" bob
marley
\"\"\"")))
    
    )
  )
