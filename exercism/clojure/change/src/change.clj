(ns change)

(defn give-change [amount coins]
  (let [coin-change (reduce (fn [{:keys [change] :as result} coin]
                              (-> result
                                  (assoc :change (rem  change coin))
                                  (assoc coin    (quot change coin))))
                            {:change amount}
                            (reverse (sort coins)))]
    (if (zero? (:change coin-change))
      (dissoc coin-change :change)
      {})))

(comment
  (give-change 10 [3 8 20])
  ;;
  )
(defn coin-count [change-m]
  (apply + (vals change-m)))

(comment
  (coin-count {}))
(defn optimize-coins [change coins]
  (->> coins
       (remove (partial < change))
       sort))

(defn best-change [amount coins-xs]
  (loop [coins  (optimize-coins amount coins-xs)
         winner {}]
    (if (not (seq coins)) ;; no more coins ?
      winner
      (recur (butlast coins)
             (let [winner-score    (coin-count winner)
                   candidate       (give-change amount coins)
                   candidate-score (coin-count candidate)]
               (if (or (empty? winner)
                       (< candidate-score winner-score))
                 candidate
                 winner))))))



(defn change-too-small? [change coins]
  (< change (apply min coins)))

(defn issue [change coins]
  (cond
    (zero?    change)                 []
    (neg-int? change)                 (throw  (IllegalArgumentException. "cannot change"))
    (change-too-small? change coins)  (throw  (IllegalArgumentException. "cannot change"))
    :else (let [coins-given (best-change change coins)]
            (if (empty? coins-given)
              (throw  (IllegalArgumentException. "cannot change"))
              coins-given))))

(comment

  (best-change 15 #{1 5 10 25 100})
  (best-change 23 #{1 4 15 20 50})
  (best-change 63 #{1 5 10 21 25})
  (best-change 10 #{20 8 3})

  (coin-count (give-change 23 #{1 5 10 25 100}))

  (loop [coins #{1 4 15 20 50}
         change 23
         winner []]
    (if (not (seq coins))
      winner
      (recur (butlast coins)
             change
             (let [winner-score    (coin-count winner)
                   candidate       (give-change 23 coins)
                   candidate-score (coin-count candidate)]
               (if (or (empty? winner)
                       (< candidate-score winner-score))
                 candidate
                 winner)))))

  ;;
  )




(comment
  (issue 25 [1 5 10 25 100])


  (reduce (fn [{:keys [change] :as res} coin]
            (let [divisor (quot change coin)]
              (-> res
                  (assoc :change (if (zero? divisor)
                                   change
                                   (rem change coin)))
                  (assoc coin divisor))))
          {:change 23} (reverse (sort #{1 4 15 20 50})))


  (reduce (fn [{:keys [change] :as res} coin]
            (-> res
                (assoc :change (rem change coin))
                (assoc coin    (quot change coin))))
          {:change 25} (reverse (sort #{1 4 15 20 50})))
  ;;
  )

(defn divisor [n d]
  [(quot n d) (rem n d)])

(comment
  (divisor 15 2)
  (divisor 15 4))

;; 15 3
;; 
