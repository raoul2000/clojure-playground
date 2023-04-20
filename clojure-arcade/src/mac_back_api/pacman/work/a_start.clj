(ns mac-back-api.pacman.work.a-start)


(defn manhattan-distance [[x1 y1] [x2 y2]]
  (+ (abs (- x2 x1))
     (abs (- y2 y1))))

(comment
  (manhattan-distance [1 1] [2 2])
  (manhattan-distance [1 1] [5 2])
  ;; 0 1 2 3 4 5

  ;;
  )

(defn int>=0? [n]
  (and (int? n)
       (> n -1)))

(defn at-position
  "Given a *grid* returns item at position *coord*, an array [x y].
   
   Returns `nil` if index out of bonds"
  [grid [x y]]
  {:pre [(int? x) (int? y)]}
  (try (-> grid
           (nth y)
           (nth x))
       (catch IndexOutOfBoundsException _ nil)))

(comment

  (not (neg-int? 1))

  (int>=0? -1)
  (int>=0? 1.2)
  (int>=0? -1.21)
  (int>=0? 1)
  (int>=0? 1.2)
  (int? 1.2)
  (neg-int? -1.2)

  (pos? 0)
  (def grid-1 [[:a :b :c :d]
               [1  2  3  4]
               [:W :X :Y :Z]])
  (def at (partial at-position grid-1))

  (at [0 0])
  (at [0 1])
  (at [3 2])

  (at [3 2])
  (at [3 3])
  (at [-1 3])
  (at [0 -1])
  (at [0.1 -1])

  ;;
  )

(defn adjacent-pos
  "Returns a vector of pos adjacent to *x1, y1*
   considering only up/down/left/right moves"
  [[x1 y1]]
  [[(inc x1) y1]
   [(dec x1) y1]
   [x1 (inc y1)]
   [x1 (dec y1)]])


(defn possible-moves [[x y] free-pos?]
  (filter free-pos? (adjacent-pos [x y])))

(defn free-pos? [grid [x y]]
  (not= "W" (at-position grid [x y])))

(comment
  (def grid-1 [[:a "W" :c :d]
               [1  2  3  4]
               [:W :X :Y :Z]])

  (rem 4 4)

  (defn index->pos [grid index]
    [(rem index (count (first grid)))
     (quot index (count (first grid)))])

  (quot 3 4)
  (index->pos grid-1 4)
  (index->pos grid-1 0)
  (index->pos grid-1 1)
  (index->pos grid-1 2)
  (index->pos grid-1 3)
  (index->pos grid-1 4)
  (index->pos grid-1 5)

  (->> grid-1
       flatten
       (keep-indexed #(when (= :Z %2) %1))
       (map (partial index->pos grid-1)))




  (#{1 2} 1)




  (possible-moves [1 1] (partial free-pos? grid-1))
  ;;
  )