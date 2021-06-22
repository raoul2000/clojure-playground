(ns anagram-wip
  (:require [clojure.string :as s]
            [clojure.data :as dta]))


(defn anagram? [u-word u-anagram]
    (when (not= u-word u-anagram)
      (let [[a b _] (clojure.data/diff (sort u-word) (sort u-anagram))]
        (= nil a b))))

;;(->> (clojure.data/diff (sort u-word) (sort u-anagram))
;;     (apply (fn [a b _] (= nil a b))))

(defn anagram?-2 [word anagram]
  (let [uc-word    (s/upper-case word)
        uc-anagram (s/upper-case anagram)]
    (and
     (not=  uc-word uc-anagram)
     (zero? (compare (sort uc-word) (sort uc-anagram))))))

(defn anagrams-for-1 [word prospect-list]
  (filter (partial anagram? word) prospect-list))

(defn anagrams-for [word prospect-list]
  (filter (comp (partial anagram? (s/upper-case word)) s/upper-case) prospect-list))

(comment

  ;; turn a string into a set
  (set "abc")

  ;; check anagram : using set as predicate is not good
  ;; as duplicates are removed from set
  (remove #{\a \b} "ab")
  (remove (set "listen") "inletsssss")

  ;; using reduce is better ?
  ;; not this one (same proble with duplicates)
  (reduce #(remove (partial = %2) %1) "listen" "inletss")
  (remove (partial = \A) "Ab")

  ;; what about sort and compare ?
  ;; => [nil nil [1 2 3]]
  (clojure.data/diff [1 2 3] [1 2 3])
  (sort "listen")
  (sort "inlets")
  (clojure.data/diff (sort "listen") (sort "inlets"))
  (clojure.data/diff [1 2] [12 3])

  ;;... as a function :
  (anagram? "listen" "inlets")
  (anagram? "listen" "Inlets")
  (anagram? "listen" "LISTEN")
  (anagram? "listen" "inletss")
  (anagrams-for "allergy" ["gallery" "ballerina" "regally"
                           "clergy"  "largely"   "leading"])

  (anagrams-for "Orchestra" ["cashregister" "Carthorse" "radishes"])
  ;;
  )
