
(ns anagram-wip2
  (:require [clojure.string :refer [upper-case]]))

(defn test-anagram [word anagram]
  (= word (sort anagram)))

(defn anagrams-for-1 [word prospect-list]
  (let [anagram? (partial test-anagram (sort (upper-case word)))]
    (filter (comp anagram? upper-case) prospect-list)))

(defn anagrams-for [word prospect-list]
  (let [anagram?     (partial test-anagram (sort (upper-case word)))
        not-equal?   (partial not=         (upper-case word))
        anagram-for? (comp (every-pred not-equal? anagram?) upper-case)]
    (filter anagram-for? prospect-list)))


(comment
  (anagrams-for "BANANA" ["banana"])

  (anagrams-for "allergy" ["gallery" "ballerina" "regally"
                           "clergy"  "largely"   "leading"])

  (map upper-case  ["gallery" "ballerina" "regally"
                    "clergy"  "largely"   "leading"])
  (filter (some-fn
           not=
           (comp anagram? upper-case)))
  ;;
  )