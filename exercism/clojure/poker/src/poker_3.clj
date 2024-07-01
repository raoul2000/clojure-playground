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

(comment
  (highest-cards "JA")
  (highest-cards "JA 2B QC")
  ;;
  )

(defn highest-card-score [hand]
  (->> hand
       highest-cards
       ffirst
       int))

(comment
  (highest-card-score "2Z 4F JZ")
  (highest-card-score "JZ")
  ;;
  )

(defn score-by-card [hand]
  (->> hand
       (map #(int (first %)))
       (apply +)))

(defn compare-card-score [h1 h2]
  (let [h1-highest-card-score (highest-card-score h1)
        h2-highest-card-score (highest-card-score h2)]
    (cond
      (> h1-highest-card-score h2-highest-card-score) -1
      (< h1-highest-card-score h2-highest-card-score)  1
      :else                                            0)))

(comment
  (sort-by identity compare-card-score ["3a 2b" "1a 1e"])
  (sort-by identity compare-card-score ["3a 2b" "2a 1e" "2x 3h"])
  ;;
  )


(defn best-hands-by-card [hands]
  (reduce (fn [winners hand]
            (let [winner-score (highest-card-score (first winners))
                  hand-score   (highest-card-score hand)]
              (cond
                (= hand-score winner-score) (conj winners hand)
                (> hand-score winner-score) [hand]
                :else                       winners))) [(first hands)] (rest hands)))

(defn best-hands [hands]
  (best-hands-by-card hands))

(comment
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

