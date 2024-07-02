(ns poker-3
  (:require [clojure.string :as s]
            [clojure.spec.alpha :as spec]))

(defn card-score [card]
  (int (first card)))

(defn highest-cards
  "Given a hand of cards, returns a vector of cards having the highest values."
  [hand]
  {:pre  [(string? hand)]
   :post [(vector? %)
          (pos? (count %))]}
  (let [[first-card & remain-cards] (s/split hand  #" ")]
    (reduce (fn [highest card]
              (let [highest-card-socre (card-score (first highest))
                    this-card-score    (card-score card)]
                (cond
                  (= this-card-score highest-card-socre) (conj highest card)
                  (> this-card-score highest-card-socre) [card]
                  :else                                  highest)))
            [first-card] remain-cards)))

(defn highest-card-score [hand]
  (->> hand
       highest-cards
       ffirst
       int))

(defn score-by-card [hand]
  (->> (s/split hand  #" ")
       (map #(int (first %)))
       (apply +)))

(defn compare-card-score [h1 h2]
  (let [h1-highest-card-score (highest-card-score h1)
        h2-highest-card-score (highest-card-score h2)]
    (cond
      (> h1-highest-card-score h2-highest-card-score) -1
      (< h1-highest-card-score h2-highest-card-score)  1
      :else                                            0)))


(defn sort-by-card-score [hands]
  (into [] (sort-by identity compare-card-score hands)))

(defn best-hands-by-card [hands]
  (reduce (fn [winners hand]
            (let [winner-score (highest-card-score (first winners))
                  hand-score   (highest-card-score hand)]
              (cond
                (= hand-score winner-score) (conj winners hand)
                (> hand-score winner-score) [hand]
                :else                       winners))) [(first hands)] (rest hands)))

(defn best-hands [hands]
  (->> hands
       best-hands-by-card
       sort-by-card-score
       (partition-by score-by-card)
       first))

(comment
  ;; keep tie


  (partition-by score-by-card ["1A" "1V" "2E" "7T"])

  (defn keep-high-card-tie [sorted-hands]
    (filter #(= (score-by-card (first sorted-hands))
                (score-by-card %)) sorted-hands))

  (def s-hands ["3S 5H 6S 8D 7H"
                "2S 5D 6D 8C 7S"])
  (map #(score-by-card %) s-hands)
  (score-by-card "3S 5H 6S 8D 7H")
  (keep-high-card-tie ["3S 5H 6S 8D 7H"
                       "2S 5D 6D 8C 7S"])

  (best-hands ["3S 5H 6S 8D 7H"
               "2S 5D 6D 8C 7S"])

  (best-hands ["4D 5S 6S 8D 3C"
               "2S 4C 7S 9H 10H"
               "3S 4S 5D 6H JH"
               "3H 4H 5C 6C JD"])

  (best-hands ["4D 5S 6S 8D 3C"
               "2S 4C 7S 9H 10H"
               "3S 4S 5D 6H JH"])
  (sort ["KS 4S 5D 6H JH"])

  (first [(first ["4D 5S 6S 8D 3C"
                  "2S 4C 7S 9H 10H"
                  "3S 4S 5D 6H JH"])])
  (highest-card-score "4D 5S 6S 8D 3C")
  (highest-cards "4D 5S 6S 8D 3C")
  (sort ["4D 5S 6S 8D 3C"
         "2S 4C 7S 9H 10H"
         "3S 4S 5D 6H JH"])

  ;;
  )

(comment
  ;; highest score for card only hand

  (defn card-value-2 [s]
    (let [[_ value ] (re-matches #"^(.+).$" s)] 
      (case value
        "K"  13
        "Q"  12
        "J"  11
        "A"  1
        (Integer/parseInt value))))
  
  (card-value-2 "1A")
  (card-value-2 "JA")
  

  (defn card-score [card]
    (case (first card)
      \1))
  (case \J
    \J 11
    \Q 12
    \K 13)

  (#{\J \Q \K} \J)
  (first "AB")
  (def MAX_1 (+ (int \K)))


  ;;
  )

