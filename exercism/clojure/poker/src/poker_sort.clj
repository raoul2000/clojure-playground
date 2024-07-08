(ns poker-sort
  (:require [clojure.string :as s]))

;; This is an attempt to solve the Poker exercism by defining a sort order
;; on all hands

(defn card-value
  "Given a single card, returns its value as a number."
  [^String s ace-rank-low]
  (let [[_ value] (re-matches #"^(.+).$" s)]
    (case value
      "K"  13
      "Q"  12
      "J"  11
      "A"  (if ace-rank-low 1 14)
      (Integer/parseInt value))))

(defn hand-card-values
  "Given a hand, returns a sorted list of card values in this hand. Lowest card value
   is first, highest card value is last."
  [hand ace-rank-low]
  (->> (s/split hand #" ")
       (map #(card-value % ace-rank-low))
       sort))

(def high-card? (constantly true))

(defn n-cards-with-same-val [hand n]
  (->> (s/split hand #" ")
       (map #(re-matches #"^(.+).$" %))
       (map second)
       frequencies
       (filter #(= n (second %)))))

(defn one-pair? [^String hand]
  (= 1 (count (n-cards-with-same-val hand 2))))

(defn two-pair? [^String hand]
  (= 2 (count (n-cards-with-same-val hand 2))))

(defn three-of-a-kind? [^String hand]
  (= 1 (count (n-cards-with-same-val hand 3))))

(defn consecutive-values? [coll]
  (= #{1} (into #{} (map (fn [[a b]] (- b a)) (partition 2 1 coll)))))

(defn straight? [hand]
  (let [ace-rank-low  (hand-card-values hand true)
        ace-rank-high (hand-card-values hand false)]
    (or (consecutive-values? ace-rank-low)
        (consecutive-values? ace-rank-high))))

(defn flush? [hand]
  (let [suits  (->> (s/split hand #" ")
                    (map last)
                    distinct)]
    (= 1 (count suits))))

(defn full-house? [hand]
  (and  (three-of-a-kind? hand)
        (one-pair?        hand)))

(defn four-of-a-kind? [hand]
  (= 1 (count (n-cards-with-same-val hand 4))))

(defn straight-flush? [hand]
  (and  (flush?    hand)
        (straight? hand)))

;; sort by hand rank

(defn hand-rank-score [hand]
  (cond
    (straight-flush?  hand)  [:straight-flush  8]
    (four-of-a-kind?  hand)  [:four-of-a-kind  7]
    (full-house?      hand)  [:full-house      6]
    (flush?           hand)  [:flush           5]
    (straight?        hand)  [:straight        4]
    (three-of-a-kind? hand)  [:three-of-a-kind 3]
    (two-pair?        hand)  [:two-pair        2]
    (one-pair?        hand)  [:one-pair        1]
    (high-card?       hand)  [:high-card       0]))

(defn sort-by-rank [hands]
  (->> hands
       (map #(conj (hand-rank-score %) %))
       (sort-by second >)))

(defn highest-hands-by-rank [hands]
  (->> hands
       (sort-by-rank)
       (partition-by first)))

(comment

  (sort-by-rank ["2A 3B 6B JB 3A" ;; high card
                 "JA 10A 9A 8A 7A"
                 "JA 10A 9A 8A 7A"
                 "2A 2B 2X 2C 3A"
                 "8A 3B 6B JB 3A"])
  (sort-by second >
           (map #(conj (hand-rank-score %) %) ["2A 3B 6B JB 3A" ;; high card
                                               "JA 10A 9A 8A 7A"
                                               "JA 10A 9A 8A 7A"
                                               "2A 2B 2X 2C 3A"
                                               "8A 3B 6B JB 3A"]))  ;; high card
  (def r1 '([:straight-flush 8 "JA 10A 9A 8A 7A"]
            [:straight-flush 8 "JA 10A 9A 8A 7A"]
            [:four-of-a-kind 7 "2A 2B 2X 2C 3A"]
            [:one-pair 1 "2A 3B 6B JB 3A"]
            [:one-pair 1 "8A 3B 6B JB 3A"]))

  (->> (first (partition-by first r1)))

  ;;
  )

;; tie hands ----------------------------------------------------------
;; finding the winner depends on hand rank

;; high cards : assign score to each hand and get the highest (sort is not enough)

(defn tie-high-card 
  "Given a coll of hands, all with high-card, returns the coll of winner
   hands."
  [hands]
  (->> hands
       (map (fn [hand]
              (vector  (into [] (reverse (hand-card-values hand true))) hand)))
       (reduce (fn [[winner :as all-winners] hand]
                 (if (seq winner)
                   (case (compare (first winner) (first hand))
                     0   (conj all-winners hand)
                     -1  [hand]
                     1   all-winners)
                   (vector hand))) [])
       (map second)))

(comment
  (tie-high-card ["4D 5S 6S 8D 3C"
                  "2S 4C 7S 9H 10H"
                  "3S 4S 5D 6H JH"])

  (->> (tie-high-card ["4D 5S 6S 8D 3C"
                       "2S 4C 7S 9H 10H"
                       "3X 4X 5X 6T JE"
                       "3S 4S 5D 6H JH"])

       (reduce (fn [[winner :as all-winners] hand]
                 (if (seq winner)
                   (case (compare (first winner) (first hand))
                     0   (conj all-winners hand)
                     -1  [hand]
                     1   all-winners)
                   (vector hand))) [])
       (map second))

  (reduce (fn [[winner :as all] hand]
            (if (seq winner)
              (case (compare winner hand)
                0   (conj all hand)
                -1  [hand]
                1   all)
              (vector hand))) [] [[10 5 8] [10 5 7] [10 5 8]])

  (apply + (map-indexed (fn [idx v]
                          (* v (inc idx))) [4 6 9 10]))
  ;;
  )










(defn compare-card-values [h1 h2]
  (compare (apply vector h1) (apply vector h2)))

(defn sort-high-cards [hands]
  (->> hands
       (map #(reverse (hand-card-values % true))) ;; ace-rank-low 
       (map #(apply vector %))

       #_(sort-by compare-card-values)))

(comment
  (sort '([16 5 4 3 2] [16 5 4 3 2] [11 5 4 3 2]))
  (sort-by identity compare [[1 4] [1 3] [1 6]])
  (sort-by identity  '([1 4] [1 3] [1 6]))

  (sort-by #(into [] %) '('(1 4) '(1 3) '(1 6)))
  (sort-by last '('(1 4) '(1 3) '(1 6)))


  (sort-by #(apply vector %) compare-card-values '('(6 5 4 3 2) '(7 5 4 3 2) '(11 5 4 3 2)))
  (compare-card-values '(7 5 4 3 2) '(7 5 4 3 3))

  (compare (apply vector '(1 2 3))  (apply vector '(1 2 3)))

  (sort-high-cards ["2A 3B 4C 5D 6T"
                    "2A 3B 4C 5D 7T"
                    "2A 3B 4C 5D JT"])
  (defn high-card [h1 h2])
  (map vector [1 2 3] [1 2 4])


  (reduce (fn [acc [c1 c2]]
            (cond
              (> c1 c2) (reduced 1)
              (< c1 c2) (reduced -1)
              :else acc)) 0 '([1 1] [2 2] [4 3]))

  (map #(apply - %)  '([1 1] [2 2] [4 3]))
  (first (drop-while zero? [0 0]))

  (sort-by high-card ["2A 3B 4C 5D 6T"
                      "2A 3B 4C 5D 7T"
                      "2A 3B 4C 5D JT"])

  ;;
  )