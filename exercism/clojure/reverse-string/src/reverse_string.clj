(ns reverse-string)

(defn reverse-string [s] ;; <- arglist goes here
  (let [v (vec (seq s))]
    (loop [pos (dec (count v))]
      (if-not
       (= pos 0) (recur (dec pos))
       ))))


(comment
  (reverse-string "abc")
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