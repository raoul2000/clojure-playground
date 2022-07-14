(ns sieve)


(defn remove-multiples-of-first-1 [xs]
  (remove #(zero? (rem %1 (first xs))) xs))


(defn remove-multiples-of-first-2 [int-xs]
  (let [first-prime (first int-xs)]
    (last (first (drop-while (fn [[n _]]
                               (<= (* n first-prime) (last int-xs)))
                             (iterate (fn [[n xs]]
                                        [(inc n) (remove #(= % (* first-prime n)) xs)])
                                      [first-prime (rest int-xs)]))))))
;; not using div, /, mod
(defn remove-multiples-of-first [int-xs]
  (let [first-prime (first int-xs)
        last-int    (last int-xs)]
    (->> [first-prime (rest int-xs)]
         (iterate (fn [[multiplier xs]]
                    [(inc multiplier) (remove #(= % (* first-prime multiplier)) xs)]))
         (drop-while (fn [[multiplier _]]
                       (<= (* multiplier first-prime) last-int)))
         first
         last)))


(comment

  (remove-multiples-of-first [2 3 4 5])
  (remove #(zero? (rem %1 2)) (range 2 (inc 10)))

  (iterate (fn [[n xs]]
             [(inc n) (remove #(= % (* 2 n)) xs)])
           [2 (range 3 11)])
  ;;
  )

(defn sieve [n]
  (loop [num-xs (range 2 (inc n))
         result []]
    (if (empty? num-xs)
      result
      (recur (remove-multiples-of-first num-xs)
             (conj result (first num-xs))))))

(comment
  (sieve 7)
  (sieve 10)
  (last (sieve 1000))
  (time (sieve 50000))
  ;;
  )
