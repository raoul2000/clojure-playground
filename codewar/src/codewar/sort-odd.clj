(ns codewar.sort-odd)

;; https://www.codewars.com/kata/578aa45ee9fd15ff4600090d/train/clojure

(defn sort-array [xs]
  (let [indexed (map-indexed vector xs)
        placeholder (vec (repeat (count xs) nil))
        parts (group-by #(odd? (first %)) indexed)
        odds (parts true)
        evens (parts false)
        sorted-evens (sort-by second  evens)
        re-index (map-indexed (fn [new-idx [_ v]] [new-idx v]) sorted-evens)
        result (concat re-index odds)]
    (reduce (fn [acc [pos v]] (assoc acc pos v)) placeholder result)))


(comment
  (map-indexed vector [9 8 7])
  (map-indexed (fn [new-idx [_ v]] [new-idx v]) '([0 :z] [2 :d] [1 :u]))


  (partition-by #(odd? (second %)) '([0 9] [1 8] [2 7]))

  (partition-by even? [1 2 3 4 5])
  (group-by #(odd? (second %)) '([0 9] [1 8] [2 7]))


  (interleave [1 2 3] [:a :b])
  (interleave [1 2 3] [:a :b :d])
  (for [n (map-indexed vector [5 3 2 8 1])
        :when (odd? (first n))]
    n)
  ({true [[0 9] [2 7]], false [[1 8]]} true)

  (take 4 (range 0 10 2))
  (sort-by second '([0 9] [1 8] [2 7]))

  (vec (repeat 5 nil))
  (assoc [nil nil nil] 2 9)

  (reduce (fn [acc [pos v]] (assoc acc pos v)) [nil nil nil] '([0 :z] [2 :d] [1 :u]))
  (sort-array [5 3 2 8 1 4])
  (= (sort-array [5 3 2 8 1 4]) [1 3 2 8 5 4])
  ;;
  )