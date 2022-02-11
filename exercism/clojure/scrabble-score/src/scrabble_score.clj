(ns scrabble-score
  (:require [clojure.string :refer [upper-case]]))

(def score {#{\A \E, \I, \O, \U, \L, \N, \R, \S, \T} 1
            #{\D \G}                                 2
            #{\B, \C, \M, \P}                        3
            #{\F, \H, \V, \W, \Y}                    4
            #{\K}                                    5
            #{\J \X}                                 8
            #{\Q \Z}                                10})

(defn score-char [^Character c]
  (->> (filter #((key %) c) score)
       first
       val))

(defn score-letter  [^String s]
  (score-char (first (upper-case s))))

(defn score-word [^String s]
  (reduce #(+ %1 (score-char %2)) 0 (upper-case s)))


