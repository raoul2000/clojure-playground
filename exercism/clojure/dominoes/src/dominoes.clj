(ns dominoes)

(defn ends-match? [chain]
  (= (ffirst chain) (last (last chain))))

(comment
  (ends-match? [[1 2] [4 1] [2 3]])
  (ends-match? [[1 2] [4 1] [2 1]])
  )

(defn chain? [chain]
  (pos-int? (reduce (fn [res [fst lst]]
                      (cond
                        (or (nil? res)
                            (= res fst))  lst
                        :else -1)) nil chain)))

(defn valid-chain? [chain]
  (and (chain?      chain)
       (ends-match? chain)))

(comment
  (valid-chain? [[1 2] [4 1] [2 3]])
  (valid-chain? [[1 2] [2 3] [3 1]])
  (valid-chain? [[1 2] [2 3] [3 2]])
  ;;
  )

(defn permutations [colls]
  (if (= 1 (count colls))
    (list colls)
    (for [head colls
          tail (permutations (disj (set colls) head))]
      (cons head tail))))

(comment
  (permutations [[1 2] [4 1] [2 3]])
  (filter valid-chain? (permutations [[1 2] [1 3] [2 3]]))
  (permutations [1 2 3])
  (permutations [1 2 3 4 5 6 7 8 9])
  ;;
  )

(defn can-chain? [stones] ;; <- arglist goes here
  (cond
    (zero? (count stones))    true
    (= 1 (count stones))      (= (ffirst stones) (last (first stones)))
    :else false))
