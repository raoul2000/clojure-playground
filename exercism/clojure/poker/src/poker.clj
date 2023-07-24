(ns poker
  (:require [clojure.string :refer [split join]]))

(defn normalize-card [^String card]
  (let [[_ rank suit] (re-matches #"([0-9JQK]+)(.)" card)
        figure-vals   {"J" 11
                       "Q" 12
                       "K" 13}]
    (try
      [(or (figure-vals rank)
           (Integer. rank))
       suit]
      (catch Exception e
        (throw (ex-info (str "failed to normalize card " card)
                        {:cause (.getMessage e)}))))))

(comment
  (normalize-card "3H")
  (normalize-card "JH")
  (normalize-card "2S")
  (normalize-card "2C")
  (normalize-card "KH")
  (normalize-card "10H")
  (normalize-card "AC")
  ;;
  )


(defn normalize-hand
  "Given a string representing a list of cards, returns a vector, 
   where each item is represents a card as a pair [rank suit]"
  [s]
  (->> (split s #" ")
       (map normalize-card)))

(comment
  (normalize-hand "4S 3D")
  (normalize-hand "4S 3D KH 3C")
  ;;
  )

(defn hand->string
  "Converts *v* a normalized form hand into a string"
  [v]
  (->> v
       (map (fn [[rank suit]] (str rank suit)))
       (join " ")))

(comment
  (hand->string (normalize-hand "4S 3D"))
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
       (frequencies)
       (vals)
       (filter #{2})
       (count)))

(comment
  (pair-count [[1 "E"] [3 "B"] [2 "R"]])
  (pair-count [[1 :e] [2 :b] [1 :r] [2 :t]])
  ;;
  )



(defn n-of-a-kind? [n hand]
  (->> hand
       (map first)
       (frequencies)
       (vals)
       (some #{n})))

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
  (let [ranks (map first hand)]
    (loop [rk ranks]
      (cond
        (empty? (rest rk))  true
        (not= (first rk) (inc (fnext rk))) false
        :else (recur (rest rk))))))

(comment

  (straight? [[1 :e] [3 :b] [2 :r]])
  (straight? [[1 :e] [2 :b] [1 :r]])
  (straight? [[1 :e] [2 :b] [1 :r] [1 :t]])
  (straight? [[5 :e] [4 :b] [3 :r] [2 :t]])
  (straight? [[5 :e] [4 :b] [3 :r] [2 :t] [1 :t]])
  (straight? [[11 :e] [10 :b] [9 :r] [8 :t] [7 :t]])
  ;;
  )

(defn flush? [hand]
  (let [suits (map second hand)]
    (every? #{(first suits)} suits)))

(comment
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
  (straight-flush? [[5 :a] [4 :a] [3 :a] [2 :a] [1 :a]])
  (straight-flush? [[5 :a] [4 :a] [3 :a] [2 :a] [1 :b]])

  (sort ["4S 5S 7H 8D JC"])
  ;;
  )

;; -----------------

(defn compare-hands [hand-a hand-b]
  1)

(defn assign-score [hand]
  [(first (find-highest-card hand)) hand])

(comment

  (assign-score (normalize-hand "4D 5S 6S 8D 3C"))
  ;;
  )

(def hand-score first)

(defn best-hands [hands]
  (if (= 1 (count hands))
    [(first hands)]
    (->> hands
         (map normalize-hand)
         (map assign-score)
         (sort-by hand-score >)
         first
         second
         hand->string
         ;;
         )))

(comment
  (best-hands ["4D 5S 6S 8D 3C"
               "2S 4C 7S 9H 10H"
               "3S 4S 5D 6H JH"])
  (defn f [xs ys] (= (sort (best-hands xs)) (sort ys)))
  (sort (best-hands  ["4D 5S 6S 8D 3C"
                      "2S 4C 7S 9H 10H"
                      "3S 4S 5D 6H JH"]))
  (sort ["4S 5S 7H 8D JC"])

  ;;
  )