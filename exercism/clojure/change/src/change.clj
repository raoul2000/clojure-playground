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

(defn coin-count [change-m]
  (apply + (vals change-m)))

(defn optimize-coins [change coins]
  (->> coins
       (remove (partial < change))
       sort))

(defn best-change [amount coins-xs]
  (loop [coins  (optimize-coins amount coins-xs)
         winner {}]
    (if (not (seq coins)) ;; no more coins ?
      (not-empty winner)
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

(defn return [coins-given]
  (->> (map (fn [[coin q]]
              (repeat q coin))
            coins-given)
       ((comp sort flatten))))

(defn issue [change coins]
  (cond
    (zero?    change)                      []
    (or (neg-int? change)
        (change-too-small? change coins)) (throw  (IllegalArgumentException. "cannot change"))
    :else (if-let [coins-given (best-change change coins)]
            (return coins-given)
            (throw  (IllegalArgumentException. "cannot change")))))
