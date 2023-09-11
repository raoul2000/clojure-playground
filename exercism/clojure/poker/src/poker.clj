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

(comment
  (card->string [1 "D"])
  (card->string [10 "D"])
  (card->string [11 "D"])
  (filter identity figure-val)
  ;;
  )

(defn hand->string
  "Converts *v* a normalized cards hand into a string"
  [v]
  (->> v
       (map card->string)
       (join " ")))

(comment
  (hand->string (normalize-hand "4S 3D"))
  (hand->string (normalize-hand "4S KH JC"))
  ;;
  )

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

(comment
  (rank-high-cards  [[[2 :a] [4 :b]]
                     [[2 :a] [4 :b]]
                     [[2 :Y] [7 :X]]
                     [[2 :a] [5 :b]]
                     [[7 :a] [2 :b]]])


  (def v '([[7 :a] [2 :b]] [[2 :a] [5 :b]] [[2 :a] [4 :b]] [[2 :a] [4 :b]]))

  (partition-by (fn [h]
                  ((apply str (map first h)))) v)
  (reduce (fn [acc hand]
            (let [id (apply str (map first hand))]
              (if (= id (first acc))
                (reduced (second acc))
                ()))) [] v)

  (defn hand-vals [hand]
    (apply str (sort > (map first hand))))


  (hand-vals [[2 :e] [3 :b]])
  (partition-by hand-vals [[[1 :a] [5 :b]]
                           [[5 :C] [1 :D]]
                           [[4 :a] [2 :b]]])
  ;;
  )



(defn find-highest-card
  "Given *hand* a normalized form hand, returns the highest card"
  [hand]
  (first (sort-by first > hand)))

(comment
  (find-highest-card [[4 "D"] [5 "S"] [6 "S"] [8 "D"] [3 "C"]])

  ;;
  )

(defn pair-count
  "Given a *hand* returns the number of pair(s)"
  [hand]
  (->> hand
       (map first)
       frequencies
       vals
       (filter #{2})
       count))

(comment
  (pair-count [[1 "E"] [3 "B"] [2 "R"]])
  (pair-count [[1 :e] [2 :b] [1 :r] [2 :t]])
  (pair-count (normalize-hand "4D 2S 4S 3C 2C"))

  (frequencies (map first [[1 :e] [2 :b] [1 :r] [2 :t]]))
  (frequencies (map first [[1 :e] [2 :b] [1 :r] [2 :t]]))
  (->> (frequencies (map first [[1 :e] [2 :b] [1 :r] [4 :t]]))
       (sort-by second)
       #_first
       #_last)



  (by-freq-and-val [3 1] [2 10])

  (compare 3 2)

  (->> (frequencies (map first [[1 :e] [8 :b] [1 :r] [8 :t] [3 :y] [5 :r]]))
       (sort by-freq-and-val)
       reverse
       (reduce (fn [acc [v freq]]
                 (+ acc (* v (* 100 (dec freq))))) 0))

  ;;
  )

(def two-pair? #(= 2 (pair-count %)))
(def one-pair? #(= 1 (pair-count %)))

(defn by-freq-and-val [[v1 freq1] [v2 freq2]]
  (let [cmp (compare freq1 freq2)]
    (if (zero? cmp)
      (compare v1 v2)
      cmp)))

(defn rank-pairs-hand
  "Given a *hand* with one or two pair, returns a score suitable to compare this
   hand with other one or two pair hands"
  [hand]
  (->> (frequencies (map first hand))
       (sort by-freq-and-val)
       (reduce (fn [acc [v freq]]
                 (+ acc v (* v (* 100 (dec freq))))) 0)))


(comment

  (rank-pairs-hand (normalize-hand "4D 4S 6S 8D 3C"))
  (rank-pairs-hand (normalize-hand "5D 5S 6S 8D 3C"))
  (rank-pairs-hand (normalize-hand "5D 5S 6S 9D 3C"))

  (rank-pairs-hand (normalize-hand "5D 5S 6S 9D 9C"))
  (rank-pairs-hand (normalize-hand "8D 8S 6S 9D 9C"))
  (rank-pairs-hand (normalize-hand "8D 8S 7S 9D 9C"))

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



(def score-by-hand {straight-flush?   10
                    four-of-a-kind?   9
                    full-house?       8
                    flush?            7
                    straight?         6
                    three-of-a-kind?  5
                    two-pair?         4
                    one-pair?         3})

(defn assign-score [hand]
  (or (some (fn [[score-for score]]
              (when (score-for hand) score)) score-by-hand)
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

(def hand-score first)

(defn best-hands [hands]
  (if (= 1 (count hands))
    [(first hands)]
    (->> hands
         (map normalize-hand)
         (map (juxt assign-score identity))
         keep-top-score-hands
         #_(sort-by hand-score >)
         #_first
         #_second
         #_hand->string
         ;;
         )))

(comment
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