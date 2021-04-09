(ns reverse-string)

(defn reverse-string [s]
  (if (= 1 (count s))
    s
    (let [vector-of-chars (vec (seq s))]
      (reduce str
              (for [pos (range (dec (count vector-of-chars)) -1 -1)]
                (get vector-of-chars pos))))))



(comment
  (for [x (range 1 5)]
    x)
  (char-array "123 4")
  (reverse-string "ab ! cdef")
  (reverse-string "a")
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