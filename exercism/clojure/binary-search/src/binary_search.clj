(ns binary-search)

;; start with a list : [1 2 5 8 10 14]
;; and a search key: 2
;; find the middle index : (/ (count list) 2) = 3
;; get value at position 3 = 8
;; test 8
;; - 2 = 8 false
;; - 2 > 8 false
;; - 2 < 8 true get first half = [1 2 3 8]



(defn middle [xs]
  (quot (count xs) 2))

(comment
  (middle [1 2]))

(defn split [xs]
  (let [middle-idx (middle xs)]
    (vector (nth    xs middle-idx)
            (subvec xs 0 middle-idx)
            (subvec xs (inc middle-idx)))))

(comment
  (split [1 2 3 4])
  (split [1 2 3])
  (split [1 2])
  (split [1])
  (split '([1] [2] [3]))
  ;;
  )

(defn item-val [pair] (last  pair))
(defn item-idx [pair] (first pair))

(defn search-for [val coll]
  (let [found-item? #(= val (item-val %))]
    (loop [xs (vec (map-indexed vector coll))]
      (cond
        (= 1 (count xs)) (if (found-item? (first xs))
                           (item-idx (first xs))
                           (throw (Exception. "not found")))
        :else (let [[pivot left-part right-part] (split xs)]
                (if (= val (item-val pivot))
                  (item-idx pivot)
                  (recur (if (> val (item-val pivot))
                           right-part
                           left-part))))))))

(comment
  (vec (map-indexed vector [:a :b]))
  (search-for 4 [4])
  (search-for 4 [1 2 3])
  (search-for 2 [1 2 3])


  ;;
  )

