(ns poker-3)

(defn card-score [card]
  (int (first card)))

(defn highest-cards
  "Given a hand of cards, returns a vector of cards having the highest values."
  [hand]
  (reduce (fn [highest card]
            (let [highest-card-socre (card-score (first highest))
                  this-card-score    (card-score card)]
              (cond
                (= this-card-score highest-card-socre) (conj highest card)
                (> this-card-score highest-card-socre) [card]
                :else                                  highest)))
          [(first hand)] (rest hand)))

(comment
  (highest-cards ["JA"])
  (highest-cards ["JA" "2B" "QC"])
  ;;
  )

(defn highest-card-score [hand]
  (->> hand
       highest-cards
       ffirst
       int))

(defn score-by-card [hand]
  (->> hand
       (map #(int (first %)))
       (apply +)))


(defn best-hands [hands]
  (reduce (fn [winners hand]
            (let [winner-score (highest-card-score (first winners))
                  hand-score   (highest-card-score hand)]
              (cond
                (= hand-score winner-score) (conj winners hand)
                (> hand-score winner-score) [hand]
                :else                       winners))) [(first hands)] (rest hands)))

(comment
  (best-hands ["4D 5S 6S 8D 3C"
               "2S 4C 7S 9H 10H"
               "3S 4S 5D 6H JH"])
  ;;
  )

