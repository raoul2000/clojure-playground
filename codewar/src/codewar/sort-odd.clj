(ns codewar.sort-odd)

;; https://www.codewars.com/kata/578aa45ee9fd15ff4600090d/train/clojure

(defn sort-array-1 [xs]
  (let [indexed (map-indexed vector xs)
        placeholder (vec (repeat (count xs) nil))
        parts (group-by #(odd? (first %)) indexed)
        odds (parts true)
        evens (parts false)
        sorted-evens (sort-by second  evens)
        re-index (map-indexed (fn [new-idx [_ v]] [new-idx v]) sorted-evens)
        result (concat re-index odds)]
    (reduce (fn [acc [pos v]] (assoc acc pos v)) placeholder result)))

(defn sort-array-2 [xs]
  (let [odds-kv       (filter #(odd? (second %)) (map-indexed vector xs))
        sorted-odds-v (sort (map second odds-kv))
        odds-k        (map first odds-kv)
        sorted-kv     (map vector odds-k sorted-odds-v)]
    (reduce #(assoc (vec %1) (first %2) (second %2)) xs sorted-kv)))



(comment


  ;; === attempt 1 =================================================
  ;; extract [pos v] items where v is odd
  ;; => ([0 5] [1 3] [4 1])
  (for [n (map-indexed vector [5 3 2 8 1])
        :when (odd? (second n))]
    n)
  ;; alternative
  (filter #(odd? (second %)) (map-indexed vector [5 3 2 8 1]))

  ;; extract only list of v from [pos v]
  ;; => (5 3 1)
  (map second '([0 5] [1 3] [4 1]))
  ;; extract only list of pos from [pos v]
  ;; => (0 1 4)
  (map first '([0 5] [1 3] [4 1]))

  ;; do both extraction in a row. First list contains pos, second contains v
  ;; => [(3 1) (8 3)]
  (reduce #(identity [(conj (first %1) (first %2)) (conj (second %1) (second %2))]) [] '([1 3] [3 8]))

  ;; sort in acsending order
  ;; => (1 3 5)
  (sort '(5 3 1))

  ;; rebuild the [pos v] list
  ;; => ([0 1] [1 3] [4 5])
  (let [pos '(0 1 4)
        ordered-vals '(1 3 5)]
    (map vector pos ordered-vals))

  ;; merge back with original seq
  (reduce #(assoc %1 (first %2) (second %2)) [5 3 2 8 1] '([0 1] [1 3] [4 5]))
  ;; DONE !!
  ;; ================================================================

  (take 4 (range 0 10 2))
  (sort-by second '([0 9] [1 8] [2 7]))

  (vec (repeat 5 nil))
  (assoc [nil nil nil] 2 9)
  ;; set item by index
  (assoc [0 1] 1 :a)

  (reduce (fn [acc [pos v]] (assoc acc pos v)) [nil nil nil] '([0 :z] [2 :d] [1 :u]))
  (sort-array-2 [5 3 2 8 1 4])
  (sort-array-2 [1 21 7 8 12 2])
  (sort-array-2 [21 7 8 4])
  (sort-array-2 [9, 8, 7, 6, 5, 4, 3, 2, 1, 0])
  (= (sort-array-2 [5 3 2 8 1 4]) [1 3 2 8 5 4])

  (->> (map-indexed vector [9 8 7])
       (group-by #(odd? (first %))))
  (map-indexed (fn [new-idx [_ v]] [new-idx v]) '([0 :z] [2 :d] [1 :u]))


  ;; pad a vector with nil to the given size (here 4)
  (partition 4 4  (repeat nil) [1 2 3])
  (partition-by #(odd? (second %)) '([0 9] [1 8] [2 7]))

  (partition-by even? [1 2 3 4 5])
  (group-by #(odd? (second %)) '([0 9] [1 8] [2 7]))


  (interleave [1 2 3] [:a :b])
  (interleave [1 2 3] [:a :b :d])
  ;;
  )