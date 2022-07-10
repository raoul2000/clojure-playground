(ns octal)

(defn valid? [s]
  (re-matches #"[0-7]+" s))

(defn pow-8 [n]
  (last (take (inc n) (iterate (partial * 8) 1))))


(comment
  (pow-8 3)
  (int (Math/pow 2 10))
  ;;
  )


(comment

  (->> "10"
       reverse
       (mapv #(Character/digit % 10))
       (map-indexed (fn [pos v]  (* (pow-8 pos) v)))
       (apply +))


  ;;
  )
(defn to-decimal-0 [s]
  (if-not (valid? s)
    0
    (->> s
         reverse
         (mapv #(Character/digit % 10))
         (map-indexed (fn [pos v]  (* (pow-8 pos) v)))
         (apply +))))

(defn to-decimal [s]
  (if-not (re-matches #"[0-7]+" s)
    0
    (->> s
         reverse
         (mapv #(Character/digit % 10))
         (map-indexed (fn [pos v]  (* (int (Math/pow 8 pos)) v)))
         (apply +))))

(comment
  (to-decimal "7777")
  ;;
  )
