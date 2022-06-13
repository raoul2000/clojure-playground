(ns proverb
  (:require [clojure.string :refer [join]]))

(defn make-line [[word-1 word-2]]
  (format "For want of a %s the %s was lost." word-1 word-2))

(defn make-last-line [words]
  (format "And all for the want of a %s." (first words)))

(defn recite-1 [words]
  (if-not (seq words)
    ""
    (join "\n" (conj (->> words
                          (partition 2 1)
                          (mapv make-line))
                     (make-last-line words)))))

;; using as-> 

(defn recite [words]
  (if-not (seq words)
    ""
    (as-> words $
      (partition 2 1 $)
      (mapv make-line $)
      (conj $ (make-last-line words))
      (join "\n" $))))

(comment

  (recite ["a"])
  (recite ["a" "b" "c"])
  ;;
  )
