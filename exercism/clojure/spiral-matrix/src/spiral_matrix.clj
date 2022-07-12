(ns spiral-matrix)

(defn create-initial-state [n]
  {:pos   [0 0]    ;; the current position
   :steps []       ;; steps performed so far (vector of [x y])
   :min-y 0        ;; top horizontal border
   :max-x (dec n)  ;; right vertical border (inclusive)
   :max-y (dec n)  ;; bottom horizontal border (inclusive)
   :min-x 0})      ;; left vertical border

(defn move [direction [x y]]
  (case direction
    :right  [(inc x)      y]
    :down   [x       (inc y)]
    :left   [(dec x)      y]
    :up     [x       (dec y)]))

(defn not-reach-border [border {:keys [max-x max-y min-x min-y]} [x y]]
  (case border
    :right (<= x max-x)
    :down  (<= y max-y)
    :left  (>= x min-x)
    :up    (>= y min-y)))

(defn update-border-after-move [state direction]
  (case direction
    :right (update state :min-y inc)
    :down  (update state :max-x dec)
    :left  (update state :max-y dec)
    :up    (update state :min-x inc)))

(defn spiral-steps-reducer [state direction]
  (let [steps (take-while (partial not-reach-border direction state)
                          (iterate (partial move direction) (:pos state)))]
    (if (empty? steps)
      (reduced (-> state
                   (update :steps #(into % [(:pos state)])) ;; add last step
                   :steps))
      (-> state
          (update :steps #(into % (butlast steps)))
          (assoc  :pos  (last steps))
          (update-border-after-move direction)))))

(defn spiral-steps [initial-state]
  (reduce spiral-steps-reducer initial-state (cycle [:right :down :left :up])))

(defn step->index [matrix-size [x y]]
  (+  x (* y  matrix-size)))

(defn spiral [n]
  (case n
    0     '()
    1     '((1))
    (let [place-holders (into [] (repeat (* n n) 0))]
      (->> (create-initial-state n)
           (spiral-steps)
           (mapv (partial step->index n))
           (reduce-kv #(assoc %1 %3 (inc %2)) place-holders)
           (partition n)))))