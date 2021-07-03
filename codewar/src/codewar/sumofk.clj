(ns codewar.sumofk)

(defn som [l] (reduce + l))


(defn products
  "add value v at the begining of each seq items in xs
   hence creating a new sequence that is returned"
  [v xs]
  (map (partial cons v) xs))

(comment
  (products 1 [[2] [3]]))

(defn all-products
  " ex: (all-products 2 [1 2 3 4]) => "
  [v xs]
  (let [seq-len (count (first xs))]
    (loop [v1 v
           l1 xs
           res []]
      (if (empty? l1)
        res
        (recur
         (rest v1)
         (drop seq-len l1)
         (cons (products (first v1) l1) res))))))

(comment
  (drop 2 [1 2 3])
  (all-products [1 2 3] [[1] [2] [3]])
  (products 1 [[2] [3]])
  (products 2 [[3]])

  (all-products [1 2 3] [[1 2] [1 3] [2 3]])
  (all-products [1 2 3 4] [[1 2] [1 3] [1 4] [2 3] [2 4]])
  (products 1 [[2 3]]))
(defn combine-1 [xs m]
  (assoc m 1 (map vector xs)))

(comment
  (combine-1 [1 2 3] {}))

(defn combine-all [xs m]
  (assoc m (count xs) xs))

(comment
  (combine-all [1 2 3] {}))


{1 [[50] [55] [45]]
 2 [[50 55] [50 45] [55 45]]
 3 [[50 55 45]]}

(defn sub-lst [v lst len]
  (if (< len 2)
    (throw (IllegalArgumentException. "v must be greter than 1"))
    (loop [xs lst
           res []]
      (if (< (count xs) (dec len))
        res
        (recur
         (rest xs)
         (conj res (into [v] (take (dec len) xs))))))))

(comment
  (sub-lst 1 [2 3 4] 2)
  (sub-lst 2 [3 4] 2)
  (sub-lst 3 [4] 2)

  (sub-lst 3 [4] 4)

  (sub-lst 1 [2 3 4 5 6] 3)
  (sub-lst 2 [3 4 5 6] 3)
  (sub-lst 3 [4 5 6] 3)
  ;; etc ...
  (loop [l [1 2 3 4 5]
         res []]
    (if (empty? l)
      res
      (recur
       (rest l)
       (into res (sub-lst (first l) (rest l) 2))))))

(defn all-sub-lst [lst len]
  (loop [l lst
         res []]
    (if (empty? l)
      res
      (recur
       (rest l)
       (into res (sub-lst (first l) (rest l) len))))))

(comment
  (all-sub-lst [1 2 3 4 5 6] 3))

(defn compute-weight [lst len]
  (map #(apply + %) (all-sub-lst lst len)))

(comment
  (compute-weight [91, 74, 73, 85, 73, 81, 87] 3)
  ;;
  )

(defn choose-best-sum [t k ls]
  (if (< (count ls) k)
    nil
    (let [dist (remove (partial < t) (compute-weight ls k))]
      (when-not (empty? dist) (apply max dist)))))

(comment
  (choose-best-sum 230 3 [91, 74, 73, 85, 73, 81, 87])
  (choose-best-sum 10 3 [91, 74, 73])

  ;;
  )

(comment
  ;;(choose-best-sum 163, 3, [50, 55, 56, 57, 58])

  ;; abcde
  ;; => 1
  ;; a b c d e 
  ;; => 2
  ;; ab   ac    ad    ae   bc   bd    be   cd   ce   de
  ;; => 3
  ;;                      abc  abd   abe  acd  ace  ade
  ;;                                      bcd  bce  bde
  ;;                                                cde
  ;; => 4
  ;;                                     abcd abce abde
  ;;                                               bcde
  ;; => 5
 ;;  abcde  

  (let [v 1
        len 2] ;; must be > 1
    (loop [xs [2 3 4 5]
           res []]
      (if (< (count xs) (dec len))
        res
        (recur
         (rest xs)
         (conj res (into [v] (take (dec len) xs)))))))

  ;;
  )