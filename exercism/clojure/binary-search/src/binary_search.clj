(ns binary-search)

(defn middle [xs]
  (-> xs count (quot 2)))

(defn split [xs]
  (let [middle-idx (middle xs)]
    (vector (nth    xs middle-idx)           ;; the pivot item
            (subvec xs 0 middle-idx)         ;; the left sub-array
            (subvec xs (inc middle-idx)))))  ;; the right sub-array 

(comment
  (split [1 2 3 4])
  (split [1 2 3])
  (split [1 2])
  (split [1])
  (split '([1] [2] [3]))
  ;;
  )

(defn search-for [searched-val coll]
  (loop [indexed-vec (vec (map-indexed vector coll))] ;; preserve initial item [index value]
    (cond
      (= 1 (count indexed-vec))
      (let [[[remaining-item-idx remaining-item-val]] indexed-vec]
        (if (= searched-val remaining-item-val)
          remaining-item-idx
          (throw (Exception. "not found"))))

      :else
      (let [[[pivot-idx pivot-val] left-sub-array right-sub-array] (split indexed-vec)]
        (cond
          (= searched-val pivot-val) pivot-idx
          (> searched-val pivot-val) (recur right-sub-array)
          :else                      (recur left-sub-array))))))

(comment

  (search-for 4 [4])
  (search-for 4 [1 2 3])
  (search-for 2 [1 2 3])  )

