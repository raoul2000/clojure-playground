(ns pascals-triangle)

;; expected return
;; [     [1]  ]
;; [   [1 2 1]]
;; [  [1 3 3 1]]
;; [  [1  4 6 4 1]]
;; [ [1 5 10 10 5 1]]

(defn next-row-1 [row]
  (if (empty? row)
    [1]
    (flatten (vector 1
                     (->> (partition 2 1 row)
                          (mapv (partial apply +')))
                     1))))

(defn next-row [row]
  (flatten [1
            (->> (partition 2 1 row)
                 (mapv (partial apply +')))
            1]))


(def triangle (iterate next-row [1]))

(defn row [n]
  (last (take n triangle)))



(comment

  (row 20)
  (concat [1]
          (->> (partition 2 1 [1 3 3 1])
               (mapv (partial apply +)))
          [1])

  (flatten (vector 1
                   (->> (partition 2 1 [1N])
                        (mapv (partial apply +)))
                   1))

  (next-row [1])
  (next-row [1 2 1])
  (next-row [1 3 3 1])
  (next-row (next-row [1 3 3 1]))


  (last (take 5 (iterate next-row [1])))

  (def row []))