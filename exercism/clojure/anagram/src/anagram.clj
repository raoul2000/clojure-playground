(ns anagram
  (:require [clojure.string :refer [upper-case]]))

(defn test-anagram [word anagram]
  (= word (sort anagram)))

(defn anagrams-for [word prospect-list]
  (let [anagram?       (partial test-anagram (sort (upper-case word)))
        not-same-word? (partial not=         (upper-case word))
        anagram-for?   (comp (every-pred not-same-word? anagram?) upper-case)]
    (filter anagram-for? prospect-list)))