(ns codewar.prize-draw
  (:require [clojure.string :as s]))


;; https://www.codewars.com/kata/5616868c81a0f281e500005c/train/clojure

(defn char-sum [st]
  (reduce #(+ %1 (- (int (Character/toUpperCase %2)) 64))
          (count st)
          st))

(defn comp-entries [e1 e2]
  (let [cmp-win (compare (first e2) (first e1))]
    (if (not= 0 cmp-win)
      cmp-win
      (compare (second e1) (second e2)))))

(defn get-nth [n coll]
  (second (nth coll (dec n) [nil nil])))

(defn rank [st we n]
  (let [particip (clojure.string/split st #",")
        cnt-part (count particip)]
    (cond
      (clojure.string/blank? st) "No participants"
      (> n cnt-part)             "Not enough participants"
      :else (->> particip
                 (map vector we)
                 (map (fn [[w name]] [(* w (char-sum name)) name]))
                 (sort comp-entries)
                 (get-nth n)))))


(comment

  (rank "Paul,Jayden,Sofia,Michael,Andrew,Lily,Benjamin", [2, 2, 1, 4, 3, 1, 2], 4)

  (rank "COLIN,AMANDBA,AMANDAB,CAROL,PauL,JOSEPH" [1, 4, 4, 5, 2, 1] 4)
  (rank "" [4 2 1 4 3 1 2] 6)
  (char-sum "z")

  (sort #(compare %2 %1) [5 6 9 1 2 0 5 9])
  (sort #(compare %2 %1) [[1 "c"] [1 "b"] [2 "a"] [-1 "e"]])

  (sort #(let [cmp-win (compare (first %2) (first %1))]
           (if (not= 0 cmp-win)
             cmp-win
             (compare (second %1) (second %2))))
        [[1 "c"] [1 "b"] [2 "a"] [-1 "e"] [1 "a"] [2 "z"]])

  (sort comp-entries  [[1 "c"] [1 "b"] [2 "a"] [-1 "e"] [1 "a"] [2 "z"]])


  (sort-by (juxt first second) [[1 "c"] [1 "b"] [2 "a"] [-1 "e"]])

  ;;
  )