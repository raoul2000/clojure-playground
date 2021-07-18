(ns anagram-wip
  (:require [clojure.string :as s]
            [clojure.data :as dta]))


(defn anagram?-1 [u-word u-anagram]
  (when (not= u-word u-anagram)
    (let [[a b _] (clojure.data/diff (sort u-word) (sort u-anagram))]
      (= nil a b))))

;;(->> (clojure.data/diff (sort u-word) (sort u-anagram))
;;     (apply (fn [a b _] (= nil a b))))


(defn anagram? [u-word u-anagram]
  (and
   (not= u-word u-anagram)
   (->> u-word
        (reduce #(s/replace-first %1   %2 "") u-anagram)
        count
        zero?)))

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

  (= (sort "abc") (sort "bca"))

  (time (anagram? "abcdefgjhijklmnopqrstuvewyz" "abcdefgjhijklmnopqrstuvewyzX"))
  (time (anagram?-1 "abcdefgjhijklmnopqrstuvewyz" "abcdefgjhijklmnopqrstuvewyzX"))

  (let [s1 (apply str (repeat 50000 \c))
        s2 (str s1 "X")]
    (time (anagram? s1 s2))
    (time (anagram?-1 s1 s2)))



  (def s1 (apply str (repeat 5000 \c)))
  (def s2 (str s1 "X"))
  (time (anagram? s1 s2))
  (time (anagram?-1 s1 s2))



  (zero? (count))
  (reduce #(s/replace-first %1 (re-pattern (str %2)) "") "inletsssss" "listen")


  (s/replace-first "abc" #"a" "")
  (s/replace-first "abc" (re-pattern (str \a)) "")
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
