(ns spiral-matrix)

;; min-y = 0
;; max-y = 2
;; min-x = 0
;; max-x = 2 
;;  !   !
;; -1 2 3
;;  8 9 4
;; -7 6 5
;; 
;; move right until x = max-x
;;  then inc min-y
;; move down until y = max-y
;;  then inc max-x
;; move left until x = min-x
;;  then dec max-y
;; move up until y = min-y
;;  then dec max-y

;; loop is :right :down :left :up, :right :down ....

(defn move [direction [x y]]
  (case direction
    :right  [(inc x)      y]
    :down   [x       (inc y)]
    :left   [(dec x)      y]
    :up     [x       (dec y)]))

(defn not-reach-border [border state [x y]]
  (case border
    :right (<= x (get state :max-x))
    :down  (<= y (get state :max-y))
    :left  (>= x (get state :min-x))
    :up    (>= y (get state :min-y))))

(defn update-limit-after-move [st direction]
  (case direction
    :right (update st :min-y inc)
    :down  (update st :max-x dec)
    :left  (update st :max-y dec)
    :up    (update st :min-x inc)))


(comment
  (move  :right [1 2])
  (take 5 (iterate (partial move :right) [0 0]))
  (take-while (fn [[x y]]
                (<= x 5)) (iterate (partial move :right) [0 0]))

  (def st {:min-y 0
           :max-x 2
           :max-y 2
           :min-x 0})

  (not-reach-border :right st [1 2])
  (not-reach-border :right st [2 2])
  (not-reach-border :right st [3 2])

  (take-while (partial not-reach-border :right st)
              (iterate (partial move :right) [3 0]))

  (take-while (partial not-reach-border :right st)
              (iterate (partial move :right) [0 0]))

  (take-while (partial not-reach-border :down st)
              (iterate (partial move :down) [2 0]))

  (take-while (partial not-reach-border :left st)
              (iterate (partial move :left) [2 2]))

  (take-while (partial not-reach-border :up st)
              (iterate (partial move :up) [0 2]))

  (take 10 (cycle [:right :down :left :up]))

  (reduce (fn [result direction]
            (let [steps (take-while (partial not-reach-border direction result)
                                    (iterate (partial move direction) (:pos result)))]
              (if (empty? steps)
                (reduced (update result :steps #(into % [(:pos result)])))
                (-> result
                    (update ,,, :steps #(into % (butlast steps)))
                    (assoc  ,,, :pos  (last steps))
                    (update-limit-after-move ,,, direction)))))

          {:pos [0 0]
           :steps []
           :min-y 0
           :max-x 2
           :max-y 2
           :min-x 0}

          (cycle [:right :down :left :up])

          ;;
          )
  ;;
  )

(defn get-by-pos [matrix-size v [x y]]
  (get v (+  x (* y  matrix-size))))

(comment

  (get-by-pos 3  [0 1 2
                  3 4 5
                  6 7 8] [1 1])

  (get-by-pos 3  [0 1 2
                  3 4 5
                  6 7 8] [0 2])

  ;;
  )


(defn create-state [n]
  {:pos [0 0]
   :steps []
   :min-y 0
   :max-x (dec n)
   :max-y (dec n)
   :min-x 0})

(defn spiral-steps [state]
  (:steps (reduce (fn [result direction]
                    (let [steps (take-while (partial not-reach-border direction result)
                                            (iterate (partial move direction) (:pos result)))]
                      (if (empty? steps)
                        (reduced (update result :steps #(into % [(:pos result)])))
                        (-> result
                            (update ,,, :steps #(into % (butlast steps)))
                            (assoc  ,,, :pos  (last steps))
                            (update-limit-after-move ,,, direction)))))
                  state
                  (cycle [:right :down :left :up]))))

(defn xy->index [matrix-size [x y]]
  (+  x (* y  matrix-size)))

(defn spiral [n] ;; <- arglist goes here
  (case n
    0     '()
    1    '((1))
    :else  (let [place-holders (into [] (repeat (* n n) 0))]
             (->> (create-state n)
                  (spiral-steps)
                  (mapv (partial xy->index n))
                  (reduce-kv (fn [result k idx]
                               (assoc result idx (inc k)))
                             place-holders)
                  (partition n)))))

(comment

  (spiral 2)
  (spiral 3)
  (spiral 4)
  (range 0 9)
  (get-by-pos 3 (into [] (range 0 9)) [0 0])
  (map (partial get-by-pos 3 (into [] (range 0 9)))  (spiral 3))

  (reduce-kv (fn [result k xy]
               (assoc result (xy->index 3 xy) (inc k)))

             (into [] (repeat 9 0))
             (spiral 3))

  ;;
  )

