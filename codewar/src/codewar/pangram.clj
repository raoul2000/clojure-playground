(ns codewar.pangram
  (:require [clojure.string :refer [lower-case]]))



(defn pangram?
  [s]
  (= 26 (->> s
             lower-case
             (re-seq #"[a-zA-Z]")
             (into #{})
             count)))

(comment
  (frequencies "abc")
  (into #{} "abc")
  (->> (re-seq #"[a-zA-Z]" "The quick brown fox jumps over the lazy dog.")
       (map lower-case)
       (into #{})
       count
       #_(frequencies)
       #_(every? #(= 1 (second %))))

  (reduce (fn [acc c]
            (if (Character/isAlphabetic (int c))
              (conj acc (lower-case c))
              acc)) #{} "The quick brown fox jumps over the lazy dog.")

  (lower-case "ABC")

  ;;
  )

