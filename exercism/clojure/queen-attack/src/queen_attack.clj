(ns queen-attack
  (:require [clojure.string :refer [join upper-case]]))


(defn create-piece-at-pos
  "Given a board description, returns a function that returns the piece representation
   at a given position on the board, or nil if there is not piece at this position."
  [board]
  (let [rev-board (into {} (map (juxt second first) board))]
    (fn [pos]
      (when-let [piece-key (get rev-board pos)]
        (upper-case (name piece-key))))))


(defn board-string
  "Returns the string representation of the given board."
  [board]
  (let [piece-at-pos (create-piece-at-pos board)]
    (->> (for [x (range 0 8)
               y (range 0 8)
               :let [mark (or (piece-at-pos [x y]) "_")]]
           mark)
         (partition 8)
         (map #(str (join " " %) "\n"))
         (apply str))))


(comment
  (board-string {})
  (print (board-string {:w [2 4] :b [6 6]}))

  ;;
  )


;; queen piece moves

(defn up-left    [[x y]] [(dec x) (dec y)])
(defn up-right   [[x y]] [(inc x) (dec y)])
(defn down-left  [[x y]] [(dec x) (inc y)])
(defn down-right [[x y]] [(inc x) (inc y)])

(defn down       [[x y]] [x (inc y)])
(defn up         [[x y]] [x (dec y)])
(defn left       [[x y]] [(dec x) y])
(defn right      [[x y]] [(inc x) y])

(defn in-board?  [[x y]]  (and (< -1 x 9) (< -1 y 9)))

(def queen-moves [up-left up-right down-left down-right left right down up])

(defn can-attack [{:keys [w b]}]
  (->> queen-moves
       ;; create coll of all pos covered by piece 'w'
       (mapcat #(take-while in-board? (rest (iterate % w))))
       ;; search for pos of piece 'b'
       (drop-while #(not= b %))
       first
       ;; if found, we have an attack
       boolean))

(comment

  (boolean [1])
  (can-attack {:w [2 3] :b [4 7]})

  (take 5 (iterate up-left [5 5]))
  (take-while in-board? (rest (iterate up-left [5 5])))
  (take-while in-board? (rest (iterate up-left [1 4])))
  (take-while in-board? (rest (iterate up-left [8 8])))
  (take-while in-board? (rest (iterate up-left [0 8])))

  (def pos1 (mapcat (fn [func]
                      (take-while in-board? (rest (iterate func [5 5])))) [up-left up-right down-left down-right
                                                                           left right down up]))
  (def mark (into {} (map-indexed (fn [idx pos]
                                    (vector (keyword (str  idx)) pos)) pos1)))

  (first (drop-while #(not= [5 3] %)  pos1))

  (print (board-string mark))

  ;;
  )
