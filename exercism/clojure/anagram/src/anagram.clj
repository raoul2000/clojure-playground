(ns anagram
  (:require [clojure.string :refer [upper-case]]
            [clojure.data :refer [diff]]))

(defn test-anagram [word anagram]
  (when (not= word anagram)
    (let [[w a] (diff (sort word) (sort anagram))]
      (= nil w a))))

(defn anagrams-for [word prospect-list]
  (let [anagram? (partial test-anagram (upper-case word))]
    (filter (comp anagram? upper-case) prospect-list)))