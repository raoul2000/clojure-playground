(ns maze)

(defn manhattan-distance [[x1 y1] [x2 y2]]
  (+ (abs (- x2 x1))
     (abs (- y2 y1))))

(defn in-grid? [grid [x y]]
  (and (< -1 x (count (first grid)))
       (< -1 y (count grid))))

(defn set-at-position [grid [x y] s]
  (update grid y assoc x s))

(defn at-position
  "Given a *grid* returns item at position *coord*, an array [x y].
   
   Returns `nil` if index out of bonds"
  [grid [x y]]
  (try (-> grid
           (nth y)
           (nth x))
       (catch js/Error _ nil)))

(defn find-in-grid [grid v]
  (let [index (->> grid
                   flatten
                   (keep-indexed #(when (= %2 v) %1))
                   first)
        col-count (count (first grid))]
    (when index
      [(mod index col-count) (quot index col-count)])))

(comment
  (def grid [[:a :b :c :d]
             [:e :f :g :a]
             [:i :j :k :a]])
  ;; FIXME: find does not work
  (def grid2 (repeat 6 [1 2 3 4]))
  (->> grid2
       flatten
       (keep-indexed #(when (= %2 4) %1))

       (map #(vector
              (quot % (inc (count (first grid2)))) (quot % (dec (count grid2))))))
  (quot 20 5)
  
  ;;
  )

(defn adjacent-positions
  "Returns a vector of *[x y]* adjacent to *x1, y1*
   considering only up/down/left/right moves"
  [[x1 y1]]
  [[(inc x1) y1]
   [(dec x1) y1]
   [x1 (inc y1)]
   [x1 (dec y1)]])

(defn possible-moves [[x y] free-pos?]
  (->> (adjacent-positions [x y])
       (filter free-pos?)))
