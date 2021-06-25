(ns codewar.sudoku)

;; https://www.codewars.com/kata/529bf0e9bdf7657179000008/train/clojure

(defn valid-seq? [s]
  (empty? (remove (set s) (range 1 10))))

(defn valid-solution [board]
 ;TODO
  )

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
  )