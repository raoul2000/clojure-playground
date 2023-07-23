(ns poker-2
  (:require [clojure.string :refer [split join]]))

(def suit-key {"H" :heart
               "S" :spade
               "C" :club
               "D" :diamond})

(def figure-vals {"J" 11
                  "Q" 12
                  "K" 13})

(defn normalize-card [card]
  (let [[_ rank suit] (re-matches #"([0-9JQK]+)(.)" card)]
    [(or (figure-vals rank)
         (Integer. rank))
     (get suit-key suit)]))

(comment
  (normalize-card "3H")
  (normalize-card "JH")
  (normalize-card "KH")
  (normalize-card "10H")
  (normalize-card "AC")
  ;;
  )

(defn sort-by-rank-desc [hand]
  (reverse (sort-by first hand)))

(defn normalize-hand
  "Given a string representing a list of cards, returns a vector in descending order
   by card rank, where each card is represented as a pair [rank suit]"
  [s]
  (->> (split s #" ")
       (map normalize-card)
       (sort-by-rank-desc)))

(comment
  (normalize-hand "4S 3D")
  (normalize-hand "JE 4S 3D KH")
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
  (pair-count [[1 :e] [3 :b] [2 :r]])
  (pair-count [[1 :e] [2 :b] [1 :r]])
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
      (flush? hand)) )

(comment
  (straight-flush? [[5 :a] [4 :a] [3 :a] [2 :a] [1 :a]])
  (straight-flush? [[5 :a] [4 :a] [3 :a] [2 :a] [1 :b]])
  ;;
  )

;; -----------------

(comment
  
  (normalize-hand "4S 5H 5S 5D 5C")
  )