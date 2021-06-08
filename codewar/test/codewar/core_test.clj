(ns codewar.core-test
  (:require [clojure.test :refer [deftest is]]
            [codewar.core :refer [sum-dig-pow]]))

(defn dotest [a b ans]
  (is (= (sum-dig-pow a b) ans)))

(deftest a-test1
  (println "Basic Tests sum-dig-pow")
  (dotest 1 10 [1, 2, 3, 4, 5, 6, 7, 8, 9])
  (dotest 1 100 [1, 2, 3, 4, 5, 6, 7, 8, 9, 89])
  (dotest 10 100  [89])
  (dotest 90 100 [])
  (dotest 90 150 [135])
  (dotest 50 150 [89, 135])
  (dotest 10 150 [89, 135]))