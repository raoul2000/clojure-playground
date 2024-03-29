(ns poker
  (:require [clojure.string :refer [split join]]
            [clojure.set :refer [map-invert]]))

(def figure-val   {"J" 11
                   "Q" 12
                   "K" 13})

(def val-figure (map-invert figure-val))

(defn normalize-card
  "Given the string representation of a single cards, returns the card model
   as a vector where the first item is the card value as a number, and the second
   the card suit as a string.
   
   example: 
   ```
   (normalize-card \"JC\")
   => [11 \"C\"]
   ```
   "
  [^String card]
  (let [[_ rank suit] (re-matches #"([0-9JQK]+)(.)" card)]
    (try
      [(or (figure-val rank)
           (Integer. rank))
       suit]
      (catch Exception e
        (throw (ex-info (str "failed to normalize card " card)
                        {:cause (.getMessage e)}))))))

(defn normalize-hand
  "Given a string representing a list of cards, returns a vector, 
   where each item is represented a card as a vector [rank suit]"
  [s]
  (->> (split s #" ")
       (map normalize-card)))

(defn card->string
  "Convert a normalized card into a string"
  [[rank suit]]
  (str (if (> rank 10)
         (get val-figure rank)
         rank) suit))

(defn hand->string
  "Converts *v* a normalized cards hand into a string"
  [v]
  (->> v
       (map card->string)
       (join " ")))

;; --- ranking ----------------------------------------------------------

(defn by-high-card
  "Compare 2 high-card hands.
   Each hand is provided in its normalized form.
   "
  [h2 h1]
  (let [card-vals  (map vector (sort-by first > h1) (sort-by first > h2))]
    (reduce (fn [acc [v1 v2]]
              (let [cmp (compare v1 v2)]
                (if-not (zero? cmp)
                  (reduced cmp)
                  acc))) 0 card-vals)))

(comment
  (sort by-high-card [[[2 :a] [4 :b]]
                      [[2 :a] [4 :b]]
                      [[2 :a] [5 :b]]
                      [[7 :a] [2 :b]]])

  (by-high-card [[2 :a] [4 :b]] [[2 :a] [4 :b]])
  (by-high-card [[2 :a] [4 :b]] [[2 :a] [5 :b]])
  (by-high-card [[2 :a] [5 :c] [3 :a]] [[2 :a] [5 :b] [1 :a]])

  ;;
  )

(defn hand-vals
  "Given a normalized *hand* returns a string composed of
   ascending sorted values.
   
   Example:
   ```clojure
   (hand-vals [[3 :a] [1 :b]])
   => \"31\"
   ```
   "
  [hand]
  (apply str (sort > (map first hand))))

(defn rank-high-cards
  "Given a coll of high-cards *hands*, returns a seq of hands with highest score.
   
   Example:
   ```clojure
   (rank-high-cards [[[2 :a] [4 :b]]
                     [[2 :A] [4 :B]]
                     [[2 :Y] [7 :X]]
                     [[2 :a] [5 :b]]
                     [[7 :a] [2 :b]]])
   => ([[7 :a] [2 :b]] [[2 :Y] [7 :X]])
   ```
   "
  [hands]
  (->> hands
       (sort by-high-card)
       (partition-by hand-vals)
       first))

(defn pair-count
  "Given a *hand* returns the number of pair(s)"
  [hand]
  (->> hand
       (map first)
       frequencies
       vals
       (filter #{2})
       count))

(def two-pair? #(= 2 (pair-count %)))
(def one-pair? #(= 1 (pair-count %)))

(defn by-freq-and-val [[v1 freq1] [v2 freq2]]
  (let [cmp (compare freq1 freq2)]
    (if (zero? cmp)
      (compare v1 v2)
      cmp)))

(defn compute-score-for-pair-hands
  "Given a *hand* with one or two pair, returns a score suitable to compare this
   hand with other hands having the same pair count"
  [hand]
  (->> (frequencies (map first hand))
       (sort by-freq-and-val)
       (reduce (fn [acc [v freq]]
                 (+ acc v (* v (* 100 (dec freq))))) 0)))

(defn rank-pair-hands
  "Given  *hands* of one or two pair hands, returns the list of highed ranked hands.
   Use this function for hands with the **same count of pairs**.

   Comparing a one pair with a two pair hand is NOT the prupose of this function, and should be done
   by caller. Hands must contain only one or only two pair hands (but not a mix).
   "
  [hands]
  (->> hands
       (map (juxt compute-score-for-pair-hands identity))
       (sort-by first >)
       (partition-by first)
       first
       (map second)))

(comment

  (rank-pair-hands [[[2 :a] [2 :b] [4 :c] [6 :d]]
                    [[2 :a] [2 :b] [4 :c] [5 :d]]
                    [[2 :a] [2 :b] [4 :c] [6 :d]]])


  (compute-score-for-pair-hands (normalize-hand "4D 8S 6S 8D 3C"))

  (compute-score-for-pair-hands (normalize-hand "4D 4S 6S 8D 3C"))
  (compute-score-for-pair-hands (normalize-hand "5D 5S 6S 8D 3C"))
  (compute-score-for-pair-hands (normalize-hand "5D 5S 6S 9D 3C"))

  (compute-score-for-pair-hands (normalize-hand "5D 5S 6S 9D 9C"))
  (compute-score-for-pair-hands (normalize-hand "8D 8S 6S 9D 9C"))
  (compute-score-for-pair-hands (normalize-hand "8D 8S 7S 9D 9C"))

  ;;
  )

(defn n-of-a-kind? [n hand]
  (->> hand
       (map first)
       (frequencies)
       (vals)
       (some #{n})
       boolean))

(def three-of-a-kind? (partial n-of-a-kind? 3))
(def four-of-a-kind? (partial n-of-a-kind? 4))

(comment
  (three-of-a-kind? [[1 :e] [3 :b] [2 :r]])
  (three-of-a-kind? [[1 :e] [2 :b] [1 :r]])
  (three-of-a-kind? [[1 :e] [2 :b] [1 :r] [1 :t]])

  (n-of-a-kind? 3 [[1 :e] [2 :b] [1 :r] [3 :t]])
  (four-of-a-kind?  [[1 :e] [2 :b] [1 :r] [3 :t] [1 :e] [1 :t]])

  ;;
  )

(defn straight?
  "Given a rank sorted *hand* returns `true` if it's a straight"
  [hand]
  (when (seq hand)
    (let [ranks (map first hand)]
      (loop [rk ranks]
        (cond
          (empty? (rest rk))  true
          (not= (first rk) (inc (fnext rk))) false
          :else (recur (rest rk)))))))

(comment
  (straight? [])
  (straight? [[1 :e] [3 :b] [2 :r]])
  (straight? [[1 :e] [2 :b] [1 :r]])
  (straight? [[1 :e] [2 :b] [1 :r] [1 :t]])
  (straight? [[5 :e] [4 :b] [3 :r] [2 :t]])
  (straight? [[5 :e] [4 :b] [3 :r] [2 :t] [1 :t]])
  (straight? [[11 :e] [10 :b] [9 :r] [8 :t] [7 :t]])
  ;;
  )

(defn flush? [hand]
  (when (seq hand)
    (let [suits (map second hand)]
      (every? #{(first suits)} suits))))

(comment
  (flush? [])
  (flush? [[1 :e] [3 :b] [2 :r]])
  (flush? [[1 :e] [2 :e] [1 :e] [1 :e]])
  ;;
  )

(defn full-house? [hand]
  (->> hand
       (map first)
       (frequencies)
       (vals)
       (sort)
       (= [2 3])))

(comment
  (full-house? [[1 :e] [3 :b] [2 :r]])
  (full-house? [[1 :e] [2 :e] [1 :e] [1 :e]])
  (full-house? [[1 :e] [2 :e] [1 :e] [1 :e] [2 :t]])
  ;;
  )

(defn straight-flush? [hand]
  (and (straight? hand)
       (flush? hand)))

(comment
  (straight-flush? [[5 :a]])
  (straight-flush? [[5 :a] [4 :a] [3 :a] [2 :a] [1 :a]])
  (straight-flush? [[5 :a] [4 :a] [3 :a] [2 :a] [1 :b]])

  (sort ["4S 5S 7H 8D JC"])
  ;;
  )

;; -----------------



(def score-by-combo {straight-flush?   10
                     four-of-a-kind?   9
                     full-house?       8
                     flush?            7
                     straight?         6
                     three-of-a-kind?  5
                     two-pair?         4
                     one-pair?         3})

(defn assign-score
  "Given a normalized form *hand*, returns a number that is the 
   score for the highest combo found in the hand.
   
   For example:
   ```clojure
   (assign-score [[1 :a] [1 :b] [7 :a] [1 :d] [1 :e]])
   => 9
   ```
   Returns 0 when no combo is found.
   "
  [hand]
  (or (some (fn [[score-for score]]
              (when (score-for hand) score)) score-by-combo)
      0))

(defn keep-top-score-hands [scored-hands]
  (->> scored-hands
       (sort-by first >)
       (partition-by first)
       first))

(comment

  (keep-top-score-hands [[1 :hand-a]  [3 :hand-b] [1 :hand-c] [3 :hand-d]])
  ;;
  )

(def rank-combo {0 rank-high-cards
                 3 rank-pair-hands
                 4 rank-pair-hands})



(defn keep-best-hand
  "Given a seq of `[score hand]` with all score being equals (same combo), returns 
   the best hand based on high cards values.
   
   When *hands* contains only one item, return it.
   "
  [hands]
  (if (= 1 (count hands))
    (second (first hands))
    (let [combo-score (ffirst hands)
          rank-fn     (get rank-combo combo-score)]

      (->> hands
           (map second)
           (map (juxt rank-fn identity))
           (sort-by first <)
           (partition-by first)
           (first)
           ))))

(comment
  (keep-best-hand '([3 ([4 "D"] [5 "S"] [6 "S"] [4 "S"] [7 "H"])]
                    [3 ([10 "D"] [5 "S"] [10 "S"] [4 "S"] [7 "H"])]))

  (keep-best-hand '([3 ([10 "D"] [5 "S"] [10 "S"] [4 "S"] [9 "H"])]
                    [3 ([10 "D"] [5 "S"] [10 "S"] [4 "S"] [8 "H"])]))

  ;;
  )

(def hand-score first)

(defn best-hands [hands]
  (if (= 1 (count hands))
    [(first hands)]
    (->> hands
         (map normalize-hand)
         (map (juxt assign-score identity))
         keep-top-score-hands
         keep-best-hand
         hand->string
         ;;
         )))


(comment
  (def nothing-1    "4D 5S 6S 1S 7H")
  (def one-pair-1   "4D 5S 6S 4S 7H")
  (def one-pair-2   "1D 5S 1S 4S 7H")
  (def two-pair-1   "1D 1S 7D 4S 7H")


  (best-hands [one-pair-1 nothing-1 one-pair-2])

  (best-hands [one-pair-1 nothing-1 one-pair-2 two-pair-1])
  (best-hands ["4D 5S 6S 8D 3C"
               "2S 4C 7S 9H 10H"
               "5D 2S 6S 2D 5C"
               "3S 4S 5D 6H JH"
               "4D 4S 6S 8D 8C"])

  (best-hands ["4D 5S 6S 8D 3C"
               "2S 4C 7S 9H 10H"
               "5D 2S 6S 2D 5C"
               "3S 4S 5D 6H JH"
               "4D 4S 6S 8D 8C"])
  (defn f [xs ys] (= (sort (best-hands xs)) (sort ys)))
  (sort (best-hands  ["4D 5S 6S 8D 3C"
                      "2S 4C 7S 9H 10H"
                      "3S 4S 5D 6H JH"]))
  (sort ["4S 5S 7H 8D JC"])

  ;;
  )