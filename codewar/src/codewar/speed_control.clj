(ns codewar.speed-control)

(defn gps [s x]
  (if (>= 1 (count x))
    0
    (->> x
         (partition 2 1)
         (map (fn [[p1 p2]] (/ (* 3600 (- p2 p1)) s)))
         (apply max)
         (Math/floor)
         int)))

(comment 
  
  (->> #_(reverse [0.0, 0.19, 0.5, 0.75, 1.0, 1.25, 1.5, 1.75, 2.0, 2.25])
   [0.0, 0.19, 0.5, 0.75, 1.0, 1.25, 1.5, 1.75, 2.0, 2.25]
       (partition 2 1)
       (map (fn [[p1 p2]] (/ (* 3600 (- p2 p1)) 15)))
       (apply max)
       (Math/floor)
       int)

  (gps 15 [0.0, 0.19, 0.5, 0.75, 1.0, 1.25, 1.5, 1.75, 2.0, 2.25])
  (gps 15 [0.0, 0.23, 0.46, 0.69, 0.92, 1.15, 1.38, 1.61])

  (->> [0.0, 0.23, 0.46, 0.69, 0.92, 1.15, 1.38, 1.61]
       (partition 2 1)
       (map #(apply - %))
       (apply max))
  ;;
  )