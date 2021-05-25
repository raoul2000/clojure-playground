(ns isbn-verifier
  (:require [clojure.string :as s]))

(defn isbn? [isbn] ;; <- arglist goes here
  (if (not (or (= 8 (count isbn)) (= 11 (count isbn))))
    false
    true))

(comment
  (Character/isDigit \1)
  (filter identity "abc")

  (filter #(Character/isDigit %) [\1 \Z])
  ;; ignore non digit chars
  (filter #(not (= \- %)) "1-23X")

  ;; use RE to extract valid chars
  (re-seq #"(\d|X)" "6-232-9X")

  ;; list of int values. X is converted to 10
  (->> (re-seq #"(\d|X)" "6-232-9X")
       (map  second)
       (map #(if (= "X" %)
               10
               (Integer/parseInt %))))
  ;; same as before with one map
  ;; isbn-char->int
  (->> (re-seq #"(\d|X)" "6-232-9X")
       (map #(let [c (second %)]
               (if (= "X" c)
                 10
                 (Integer/parseInt c))))
       (into []))
  ;; but we KNOW that X is only last

  ;; reduce int list using loop : __OK__
  ;; 3598215088 mod 11 === 0
  (loop [isbn (reverse '(3 5 9 8 2 1 5 0 8 8))
         idx 10
         result 0]
    (if (empty? isbn)
      result
      (recur
       (rest isbn)
       (dec idx)
       (+ result (* (first isbn) idx)))))

  ;; (map-indexed vector "foobar")
  (map-indexed vector (reverse '(3 5 9 8 2 1 5 0 8 8)))
  (map-indexed #(* (inc %1) %2) '(1 2))
  (map-indexed #(* (inc %1) %2) (reverse '(3 5 9 8 2 1 5 0 8 8)))

  (apply + (map #(* (inc (first %)) (second %)) 
         '([0 8] [1 8] [2 0] [3 5] [4 1] [5 2] [6 8] [7 9] [8 5] [9 3])))
  
  (apply str '(1 2))

  


  (map-indexed vector (reverse '(3 5 9 8 2 1 5 0 8 8)))


  (for [c "1-23-8"
        :when (or
               (Character/isDigit c)
               (= \X c))]
    c))