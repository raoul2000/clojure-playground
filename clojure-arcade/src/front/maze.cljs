(ns maze)

(defn manhattan-distance [[x1 y1] [x2 y2]]
  (+ (abs (- x2 x1))
     (abs (- y2 y1))))

(defn in-grid? [grid [x y]]
  (and (< -1 x (count (first grid)))
       (< -1 y (count grid))))

(defn index->pos [grid index]
  (let [line-length (count (first grid))]
    [(rem index line-length)
     (quot index line-length)]))

(defn set-at-position [grid [x y] s]
  (update grid y assoc x s))

(defn get-at-position
  "Given a *grid* returns item at position *coord*, an array [x y].
   
   Returns `nil` if index out of bonds"
  [grid [x y]]
  (try (-> grid
           (nth y)
           (nth x))
       (catch js/Error _ nil)))

(defn find-in-grid [grid v]
  (when-let [index (->> grid
                        flatten
                        (keep-indexed #(when (= %2 v) %1))
                        first)]
    (index->pos grid index)))

(defn find-all-in-grid [grid v]
  (->> grid
       flatten
       (keep-indexed #(when (= %2 v) %1))
       (map #(index->pos grid %))))


(comment
  (def grid [[:a :b :c :d]
             [:e :f :g :a]
             [:i :j :k :a]])

  (find-in-grid grid :a)
  (find-all-in-grid grid :a)


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

(comment

  {:pos [0 0]    ;; current vertex pos
   :prev nil     ;; previous vertex pos
   :from-start 5
   :heuristic 10
   :prev-vertex nil
   :cost 15 ;; :from-start + heuristic =  smaller the better
   }

  {:open []
   :close []
   :current [0 0]
   :start-pos [0 0]
   :end-pos [3 3]
   :grid [[1 2 3] [4 5 6]]}

  (def open [])
  (def close []))

(defn new-open-entry [state pos]
  (let [from-start (manhattan-distance (:start-pos state) pos)
        heuristic  (manhattan-distance (:end-pos state) pos)]
    {:cur          pos
     :prev         (first (:open state))
     :from-start   from-start
     :heuristic    heuristic
     :prev-vertex  nil
     :cost         (+ from-start heuristic)}))

(comment
  (new-open-entry {:open       []
                   :close      []
                   :start-pos  [0 0]
                   :end-pos    [2 2]
                   :grid       [[1 2 3] [4 5 6]]} [0 0])

  (new-open-entry {:open       [[0 0]]
                   :close      []
                   :start-pos  [0 0]
                   :end-pos    [2 2]
                   :grid       [[1 2 3] [4 5 6]]} [1 0])

  ;; assuming OPEN list is sorted by f value
  ;;
  ;; add start pos as CURRENT
  ;;    - calculate g, h to get f
  ;;    - previous is nil
  ;; if CURRENT is end-pos 
  ;;    - STOP and return the path
  ;; else
  ;;   select adjacent to CURRENT
  ;;      for each adjacent for CURRENT
  ;;       - calculate g (dist to first OPEN), g (dist to end), 
  ;;       - previous is CURRENT
  ;;       - if pos is not already in OPEN
  ;;           add it to OPEN 
  ;;         else id pos already in OPEN but with higher f
  ;;           replace it
  ;;    move CURRENT to CLOSE
  ;;    select in OPEN a new CURRENT 
  ;;      - take lowest h
  ;;  endif
  )


(defn init-state [grid start end]
  {:open       []
   :close      []
   :current    start
   :start-pos  start
   :end-pos    end
   :grid       grid})

(defn find-adjacent-pos [grid pos]
  (let [free-pos? (fn [pos])])
  )

(defn update-state [state]
  (let [adjacent (find-adjacent-pos (:grid state) (:current state) )])
  state)

(defn build-path [state]
  "not implemented")

(defn find-path [grid start-pos end-pos]
  (loop [state (init-state grid start-pos end-pos)]
    (cond
      (= (:current state) end-pos)   (build-path state)
      (empty? (:open state))          nil ;; no path, sorry
      :else
      (recur (update-state state)))))

