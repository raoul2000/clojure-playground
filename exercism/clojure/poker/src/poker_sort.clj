(ns poker-sort
  (:require [clojure.string :as s]))

;; This is an attempt to solve the Poker exercism by defining a sort order
;; on all hands

(defn card-value [^String s ace-rank-low]
  (let [[_ value] (re-matches #"^(.+).$" s)]
    (case value
      "K"  13
      "Q"  12
      "J"  11
      "A"  (if ace-rank-low 1 14)
      (Integer/parseInt value))))

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
  (let [cards         (s/split hand #" ")
        ace-rank-low  (->> cards
                           (map #(card-value % true))
                           sort)
        ace-rank-high (->> cards
                           (map #(card-value % false))
                           sort)]
    (or (consecutive-values? ace-rank-low)
        (consecutive-values? ace-rank-high))))

(defn flush? [hand]
  (let [suits (distinct (->> (s/split hand #" ")
                             (map last)
                             distinct))]
    (= 1 (count suits))))

(defn full-house? [hand]
  (and (three-of-a-kind? hand)
       (one-pair?        hand)))

(defn four-of-a-kind? [hand]
  (= 1 (count (n-cards-with-same-val hand 4))))

(defn straight-flush? [hand]
  (and (flush?    hand)
       (straight? hand)))

;; sort by hand rank

(defn hand-rank-score [hand]
  (cond
    (straight-flush?  hand)  8
    (four-of-a-kind?  hand)  7
    (full-house?      hand)  6
    (flush?           hand)  5
    (straight?        hand)  4
    (three-of-a-kind? hand)  3
    (two-pair?        hand)  2
    (one-pair?        hand)  1
    :else                    0))

(comment

  (sort-by first >
           (map (juxt hand-rank-score identity) ["2A 3B 6B JB 3A" ;; high card
                                                 "JA 10A 9A 8A 7A"
                                                 "JA 10A 9A 8A 7A"
                                                 "2A 2B 2X 2C 3A"
                                                 "8A 3B 6B JB 3A"]))  ;; high card
  (def r1 '([8 "JA 10A 9A 8A 7A"] 
            [8 "JA 10A 9A 8A 7A"] 
            [7 "2A 2B 2X 2C 3A"] 
            [1 "2A 3B 6B JB 3A"] 
            [1 "8A 3B 6B JB 3A"]))
  
  (->> (first (partition-by first r1))
       (map second)
       )



  ;;
  )