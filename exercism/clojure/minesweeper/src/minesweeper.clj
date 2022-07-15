(ns minesweeper
  (:require [clojure.string :refer [join split]]))

(defn each-pos [[max-x max-y]]
  (for [x (range 0 max-x)
        y (range 0 max-y)]
    [x y]))

(defn neighbours [[x y]]
  (remove #(= % [x y]) (for [x' (range (dec x) (+ 3 (dec x)))
                             y' (range (dec y) (+ 3 (dec y)))]
                         [x' y'])))

(defn neighbours-in-board [[x y] max-x max-y]
  (filter (fn [[x' y']]
            (and (< -1 x' max-x)
                 (< -1 y' max-y)))
          (neighbours [x y])))

(defn get-pos [board [x y]]
  (get (get board y) x))

(defn inc-or-identity [square-val]
  (case square-val
    \*     square-val
    \space 1
    (inc square-val)))

(defn update-square [board [x y]]
  (update board y #(update % x inc-or-identity)))

(defn make-2d-board [s line-separator]
  (->> (split s (re-pattern line-separator))
       (map #(apply vector %))
       (into [])))

(defn board-size [board]
  [(count (first board))
   (count board)])

(defn mark-neighbors [board [max-x max-y] [x y]]
  (reduce (fn [board' xy-neighbour]
            (update-square board' xy-neighbour))
          board
          (neighbours-in-board [x y] max-x max-y)))

(defn mined-square? [board pos]
  (= \* (get-pos board pos)))

(defn draw [s]
  (if (= "" s)
    ""
    (let [line-separator (System/getProperty "line.separator")
          initial-board  (make-2d-board s line-separator)
          board-max-xy   (board-size initial-board)]
      (->> (reduce (fn [board pos]
                     (cond-> board
                       (mined-square? board pos) (mark-neighbors board-max-xy pos)))
                   initial-board
                   (each-pos board-max-xy))
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
