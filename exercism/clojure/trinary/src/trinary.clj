(ns trinary)

(defn power-of-3 [n]
  (int (Math/pow 3 n)))

(comment

  (power-of-3 0)
  (power-of-3 1)
  (power-of-3 2)
  (power-of-3 3)
  (power-of-3 4))


(defn to-decimal-1 [s]
  (if-not (re-matches #"[012]+" s)
    0
    (->> s
         (map #(Character/digit % 10))
         reverse
         (map-indexed #(* (power-of-3 %1) %2))
         (apply +))))

(defn to-decimal [s]
  (if-not (re-matches #"[012]+" s)
    0
    (->> (map #(* (Character/digit %1 10) %2) (reverse s) (iterate (partial * 3) 1))
         (apply +))))
