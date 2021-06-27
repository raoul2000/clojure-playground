(ns codewar.sudoku)

;; https://www.codewars.com/kata/529bf0e9bdf7657179000008/train/clojure

(defn valid-seq? [s]
  (empty? (remove (set s) (range 1 10))))

(defn test-block-line-1 [three-lines]
  (loop [l1 (first three-lines)
         l2 (second three-lines)
         l3 (last three-lines)
         valid true]
    (if (or (< (count l1) 3) (not valid))
      valid
      (recur (drop 3 l1) (drop 3 l2) (drop 3 l3)
             (let [m1 (take 3 l1)
                   m2 (take 3 l2)
                   m3 (take 3 l3)]
               (valid-seq? (concat m1 m2 m3)))))))

(defn valid-blocks-per-line? [lines]
  (loop [[l1 l2 l3] lines
         valid      true]
    (if (or (< (count l1) 3) (not valid))
      valid
      (recur [(drop 3 l1) (drop 3 l2) (drop 3 l3)]
             (valid-seq? (concat (take 3 l1) (take 3 l2) (take 3 l3)))))))

(defn valid-blocks? [board]
  (loop [lines  board
         valid  true]
    (if (or (< (count lines) 3) (not valid))
      valid
      (recur
       (drop 3 lines)
       (valid-blocks-per-line? (take 3 lines))))))

(defn inverse-matrix [m]
  (loop [m      (flatten m)
         result []]
    (if (< (count m) 73)
      result
      (recur
       (rest m)
       (conj result  (take-nth 9 m))))))

(defn valid-solution [board]
  (and
   (every? valid-seq? board)
   (every? valid-seq? (inverse-matrix board))
   (valid-blocks? board)))

;; the cleverest solution is 
;; using partition, interleave and map on a flattened coll 
;; is enough
(defn clever-valid-solution [board]
  (every? #(= (sort %) (range 1 10))
          (concat board
                  (partition 9 (apply interleave board))
                  (map flatten (partition 3 (apply interleave (map #(partition 3 %) board)))))))

(comment

  (def grid-1 [[5 3 4 6 7 8 9 1 2]
               [6 7 2 1 9 5 3 4 8]
               [1 9 8 3 4 2 5 6 7]
               [8 5 9 7 6 1 4 2 3]
               [4 2 6 8 5 3 7 9 1]
               [7 1 3 9 2 4 8 5 6]
               [9 6 1 5 3 7 2 8 4]
               [2 8 7 4 1 9 6 3 5]
               [3 4 5 2 8 6 1 7 9]])

  ;; validate lines
  (empty? (remove (set [6 7 2 1 9 5 3 4 8]) (range 1 10)))
  (valid-seq? [8 5 9 7 6 1 4 2 3])
  (valid-seq? [8 5 9 7 6 1 4 2 2])

  ;;extract colunmn n (here = 0)
  (map #(get % 0) grid-1)

  (loop [m (flatten grid-1)
         result []]
    (if (< (count m) 73)
      result
      (recur
       (rest m)
       (conj result  (take-nth 9 m)))))
  (inverse-matrix grid-1)
  
  (valid-solution grid-1)

  ;;extract block (tricky !)
  ;; keep a 2 dimensions struct
  ;; 0,0 0,1 0,2
  ;; 1,0 1,1 1,2
  ;; 2,0 2,1 2,2

  ;; x = 0 ... 6
  ;; y = 0 ... 6

  ;; flattened struct
  ;; 00 01 02
  ;; 09 10 11
  ;; 18 19 20

  ;; index
  ;; 00 01 02 03 04 05 06
  ;; 09 10 11 12 13 14 15
  ;;.... 43 + 6 = 49


  (let [x 0]
    (subvec [5 3 4 6 7 8 9 1 2]  x (+ 3 x)))

  (let [m grid-1
        lines (take 3 m)]
    (map #(subvec % 0 3) lines))

  ;; loop across all sets of 3 lines
  (loop [m     grid-1
         valid true]
    (print m)
    (if (or (< (count m) 3) (not valid))
      valid
      (recur
       ;;(nthrest m 3)
       (drop  3 m)
       true))) ;; test those 3 lines (take 3)

  ;; given 3 lines, extract all 3x3 matrix
  (loop [l1 [5 3 4 6 7 8 9 1 2]
         l2 [6 7 2 1 9 5 3 4 8]
         l3 [1 9 8 3 4 2 5 6 7]
         valid true]
    (if (or (< (count l1) 3) (not valid))
      valid
      (recur (drop 3 l1) (drop 3 l2) (drop 3 l3)
             (let [m1 (take 3 l1)
                   m2 (take 3 l2)
                   m3 (take 3 l3)]
               (println m1)
               (println m2)
               (println m3)
               (valid-seq? (concat m1 m2 m3))))))

  (valid-blocks-per-line? [[5 3 4 6 7 8 9 1 2]
                    [6 7 2 1 9 5 3 4 8]
                    [1 9 8 3 4 2 5 6 7]])
  (valid-blocks? grid-1)
  ;;
  )