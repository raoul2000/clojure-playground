(ns reverse-string
  (:require [clojure.string :as string]))

(defn reverse-string-1 [s]
  (if (= 1 (count s))
    s
    (let [vector-of-chars (vec (seq s))]
      (reduce str
              (for [pos (range (dec (count vector-of-chars)) -1 -1)]
                (get vector-of-chars pos))))))

(defn reverse-string-1 [s]
  (reduce
   str
   (into () (vec (seq s)))))

(defn reverse-string-oveflow [[first-char & rest]]
  (if (empty? rest)
    (str first-char)
    (str (reverse-string-oveflow rest) first-char)))

(defn reverse-string [s]
  (loop [s s
         result ""]
    (if (empty? s)
      result
      (recur (rest s) (str (first s) result)))))



  (def trans
    (comp
     (map #(% 2))
     (filter #(> % 3))
     (take 4)))

(map trans [1 2 3])
(into []  '(1 2 3 4 5))


(comment
  ({:name "bob"} :name)
  (string/split "abc" #"")
  (into []  [1 2 3])

  (into () [1 2])
  (into () (string/split "hello world" #""))
  (reduce
   str
   (into () (vec (seq "h"))))
  (into () (vec (seq "hello world")))
  (map / [1 2 3])

  (->>
   (range 5)
   (map #(* 2 %))
   (filter #(> % 2))
   (filter even?))






  (def str1 "hello world")
  (let [[c & rest] "hello"]
    (println c rest))
  (for [x (range 1 5)]
    x)
  (char-array "123 4")
  (reverse-string "ab ! cdef")
  (reverse-string "a")
  (reverse-string "")
  (reverse-string (repeat 1000 "overflow?"))
  (list "abc")
  (subs  "abc" 2)
  (let [s "abcd"]
    (subs s (- (count s) 2))
    (subs s 4)
    (subs s 3 4)
    (subs s 2 3)
    (subs s 1 2)
    (subs s 0 1))
  (seq "abc")
  (count (seq "abc"))
  (do
    (let [s "abcd"]
      (loop [pos (count s)]
        (cond
          (= s 0) (subs)))))
  (vec (seq "abc"))
  (count [1 2])
  (let [s "abc"]
    (vec (seq s)))
  (get [1 2 3])
  (loop [pos 4]
    (println pos)
    (if
     (= pos 0) "end"
     (recur (- pos 1)))))