(ns sum-of-multiples)


(defn multiple? [xs]
  (fn [n]
    (some #(zero? (rem n %)) xs)))

(defn sum-of-multiples [xs max]
  (->> (range 0 max)
       (filter (multiple? xs) )
       (apply +)))

(comment
  (sum-of-multiples [3 5] 20))


