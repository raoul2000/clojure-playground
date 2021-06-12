(ns codewar.prize-draw
  (:require [clojure.string :as s]))


;; https://www.codewars.com/kata/5616868c81a0f281e500005c/train/clojure

(defn char-sum [st]
  (reduce #(+ %1 (- (int (Character/toUpperCase %2)) 64))
          0
          st))


(defn rank [st we n]
  (->> (map vector (s/split st #",") we)
       (map (fn [[name w]] [name (* w (char-sum name))]))
       (sort)))


(comment

  (rank "Addison,Jayden,Sofia,Michael,Andrew,Lily,Benjamin", [4, 2, 1, 4, 3, 1, 2], 4)
  (char-sum "z")
  ;;
  )