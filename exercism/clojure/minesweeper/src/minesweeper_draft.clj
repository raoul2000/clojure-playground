(ns minesweeper-draft
  (:require [clojure.string :refer [join split]]))


(defn each-pos [max-x max-y]
  (for [x (range 0 max-x)
        y (range 0 max-y)]
    [x y]))

(comment
  (each-pos 3 3)
  ;;
  )

(defn neighbours [[x y]]
  (remove #(= % [x y]) (for [x' (range (dec x) (+ 3 (dec x)))
                             y' (range (dec y) (+ 3 (dec y)))]
                         [x' y'])))

(defn neighbours-in-board [[x y] max-x max-y]
  (filter (fn [[x' y']]
            (and (< -1 x' max-x)
                 (< -1 y' max-y)))
          (neighbours [x y])))

(comment
  (neighbours [2 2])
  (neighbours [0 0])
  (neighbours-in-board [0 0] 3 3)
  (neighbours-in-board [1 0] 3 3)
  (neighbours-in-board [2 0] 3 3)

  (neighbours-in-board [2 2] 3 3))

(defn get-pos [board [x y]]
  (get (get board y) x))


(comment
  (join \newline [" "
                  "*"
                  " "
                  "*"
                  " "])
  (def b1 [[1 2 3]
           [4 5 6]
           [7 8 9]])


  (get-pos b1 [0 0])
  (get-pos b1 [1 0])
  (get-pos b1 [2 0])
  (get-pos b1 [4 0])
  (get-pos b1 [0 1])
  (get-pos b1 [2 2])

  ;;
  )

(defn update-square [square-val]
  (case square-val
    \*     square-val
    \space 1
    (inc square-val)))

(defn inc-pos [board [x y]]
  (update board y #(update % x update-square)))

(comment

  (def b1 [[1 2 3]
           [4 5 6]
           [7 8 9]])
  (inc-pos b1 [0 0])
  (inc-pos b1 [1 1])
  (inc-pos b1 [2 2])

  (def b2 [(apply vector "  *")
           (apply vector "* *")
           (apply vector " * ")])
  (def b3 (inc-pos b2 [0 0]))
  (inc-pos b3 [0 0])
  ;;
  )

(defn make-2d-board [s line-separator]
  (->> (split s (re-pattern line-separator))
       (map #(apply vector %))
       (into [])))

(comment
  (make-2d-board "edfqsd \nqs dft" "\n")
  (def separator (System/getProperty "line.separator"))
  (def s2 (join separator ["123" "abc"]))

  (split s2 #"\r\n")
  (split s2 (re-pattern separator))

  ;;
  )
(defn board-size [board]
  [(count (first board))
   (count board)])


(defn mark-neighbors [board max-x max-y [x y]]
  (reduce (fn [board' xy-neighbour]
            (inc-pos board' xy-neighbour))
          board
          (neighbours-in-board [x y] max-x max-y)))

(comment

  (def b2 [[\space \space \* \space \space]
           [\space \space \* \space \space]
           [\* \* \* \* \*]
           [\space \space \* \space \space]
           [\space \space \* \space \space]])
  (mark-neighbors b2 5 5 [0 2]))

(defn mined-square? [board pos]
  (= \* (get-pos board pos)))

(comment

  (def b1 [[1 2 3]
           [4 \* 6]
           [7 8 9]])

  (mined-square? b1 [0 0])
  (mined-square? b1 [1 1])

  ;;
  )
(defn draw [s]
  (if (= "" s)
    ""
    (let [line-separator (System/getProperty "line.separator")
          initial-board (make-2d-board s line-separator)
          max-y (count initial-board)
          max-x (count (first initial-board))]
      [initial-board max-x max-y]
      (->> (reduce (fn [board pos]
                     (if (mined-square? board pos)
                       (mark-neighbors board max-x max-y pos)
                       board))
                   initial-board
                   (each-pos max-x max-y))
           (map #(apply str %))
           (join line-separator)))))

(comment
  (draw (join \newline [" *"
                        "* "]))
  (def line-separator (System/getProperty "line.separator"))
  (draw (join line-separator ["  *  "
                              "  *  "
                              "*****"
                              "  *  "
                              "  *  "]))
  (split "  *  \r\n  *  \r\n*****\r\n  *  \r\n  *  " #"\r\n")
  (re-pattern "\\r\\n")
  ;;
  )
