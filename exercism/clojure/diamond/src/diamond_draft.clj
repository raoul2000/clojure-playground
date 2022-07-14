(ns diamond-draft)


;; A index is 1 => ["A"]
;; B index is 2 => a 3x3 matrix
;; C index is 3 => a 5x5 matrix
;; D index is 4 => a 7x7 matrix
;; when index is idx square size is (inc (* 2 (dec idx)))

;; drawing the matrix should only require to draw 2 parts
;; - top : from the top to row idx -1
;;     - first : one char at center
;;     - rest : can be partially build, duplicated, reversed and concat
;; - middle : row (idx -1)
;; the bottom part is the same as the top part but in reverse order

;; all rows have n positions initialized to \space
;; row 0 contains letter idx=1 
;;   - count = once
;;   - pos = 
;;

(defn diamond-size [c]
  (let [letter-pos (- (int c) 64)]
    (inc (* 2 (dec letter-pos)))))

(comment
  (diamond-size \A)
  (diamond-size \B)
  (diamond-size \C)
  ;;
  )

(comment


  (apply str (concat (repeat 3 " ") [\b] (repeat 2 " ")))
  (apply str (concat (repeat 2 " ") [\b] (repeat 3 " ")))
  (apply str (reverse (concat (repeat 2 " ") [\b] (repeat 3 " "))))

  (map #(assoc (into [] (repeat 3 " ")) % (char (+ 66 %))) (range 0 3)))

(defn make-empty-line [len]
  (into [] (repeat len " ")))

(defn make-half-lines [len]
  (let [empty-line (make-empty-line len)]
    (map #(assoc empty-line  % (char (+ 66 %))) (range 0 len))))

(comment

  (make-half-lines 1)
  (make-half-lines 2)
  (make-half-lines 3)
  (make-half-lines 4)

  (map #(concat (reverse %) %)  '([\B " " " "] [" " \C " "] [" " " " \D])))


(defn flip-y [lines]
  (map #(concat (reverse %) [" "] %) lines))

(comment
  (flip-y (make-half-lines 4))
  (reverse (flip-y (make-half-lines 10)))

  ;;
  )

(defn flip-x [lines]
  (concat lines (reverse (butlast lines))))


(comment
  (->> (make-half-lines 4)
       flip-y
       flip-x
       (map #(apply str %)))

  (assoc (into [] (repeat 7 " "))  (quot 7 2) \A))

(defn make-first-line [size]
  (assoc (make-empty-line size)  (quot size 2) \A))

(comment

  (make-first-line 3)
  (make-first-line 7))

(defn diamond [c]
  (if (= \A c)
    ["A"]
    (let [size       (diamond-size c)
          first-line (make-first-line size)]
      (map #(apply str %) (concat [first-line]
                                  (->> (make-half-lines (quot size 2))
                                       flip-y
                                       flip-x)
                                  [first-line])))))

(comment
  (diamond-size \C)
  (diamond \C)

  ;;
  )
