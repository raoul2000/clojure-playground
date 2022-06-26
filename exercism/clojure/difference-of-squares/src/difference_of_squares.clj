(ns difference-of-squares)

;; first solution

(defn sum-of-squares-1 [n]
  (->> (range 1 (inc n))
       (map #(* % %))
       (apply +)))

(defn sum-of-squares [n]
  (quot (* n (inc n) (inc (* 2 n))) 6))

(defn square-of-sum-1 [n]
  (int (Math/pow (apply + (range 1 (inc n))) 2)))

(defn square-of-sum [n]
  (let [sum (quot (* n (inc n)) 2)]
    (* sum sum))

  ;;
  )

(defn difference [n]
  (- (square-of-sum n) (sum-of-squares n)))

;; let's play a bit

(comment

  (clojure.walk/walk #(* % %) #(apply + %) (range 1 (inc 5)))
  (def n 5)
  (quot (* n (inc n) (inc (* 2 n))) 6)


  ;;
  )
