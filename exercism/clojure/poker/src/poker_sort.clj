(ns poker-sort
  (:require [clojure.string :as s]))

;; first we want to assign a rank to each hand, and keep only
;; the highest hand(s)

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

(defn n-cards-with-same-val
  "Given a hand, return a coll of pairs where the first item is a card value 
   with *n* occurences in the hand, and the second item is *n*.

   Returns an empty coll if not found.
   
   Example:

   ```clojure
   (n-cards-with-same-val \"2A 5B 6T 9U 9I\" 2)
   => ([\"9\" 2]) ;; only one pair

   (n-cards-with-same-val \"2A 2B 6T 9U 9I\" 2)
   => ([\"2\" 2] [\"9\" 2]) ;; two pairs

   (n-cards-with-same-val \"2A 2B 6T 2U 9I\" 3)
   => ([\"2\" 3]) ;; three of a kind
   ```

   "
  [^String hand n]
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
    :else                    [:high-card       0]))

(defn sort-by-rank [hands]
  (->> hands
       (map #(conj (hand-rank-score %) %))
       (sort-by second >)))

(defn highest-hands-by-rank
  "Given a list of  hands, returns a vector where the first item is the highest
   ranks found, and the second is a coll of hands of this ranks."
  [hands]
  (->> hands
       (sort-by-rank)
       ;; partition by rank
       (partition-by first)
       ;; only consider the first one
       first
       ((juxt ffirst (partial map last)))))

;; tie hands ----------------------------------------------------------
;; In case more than one hand has highest rank, we must find the one with
;; highest "value"

(defn hand->scored-hand
  "Given a hand, returns a pair where the first item is a sorted list of numbers  corresponding
    to card values, and the second item is the hand itself."
  ([hand]
   (hand->scored-hand false hand))
  ([ace-rank-low hand]
   [(->> (hand-card-values hand ace-rank-low)
         frequencies
       ;; group by card occurence count
       ;; 'second' is occurence count (here 2 is expected)
         (partition-by second)
       ;; flatten 1 level depth
         (mapcat identity)
         (map (fn [[card-value cnt]]
                (+ (* 1000 (dec cnt)) card-value)))
         (sort >)
         (into []))
    hand]))

(defn scored-hand-reducer
  [[winner :as all-winners] hand]
  {:pre [(or (nil? winner)
             (vector? (first winner)))
         (vector? hand)]}
  (if (seq winner)
    (case (compare (first winner) (first hand))
      0   (conj all-winners hand)
      -1  [hand]
      1   all-winners)
    (vector hand)))

(defn tie-hands
  "Given a coll of hands, all of the same rank, returns the coll of winner
  hands."
  ([hands]
   (tie-hands hands false))
  ([hands ace-rank-low]
   (->> hands
        (map (partial hand->scored-hand ace-rank-low))
        (reduce scored-hand-reducer [])
        (map second))))

;; main function -----------------------------------------------------

(defn best-hands [hands]
  (let [[highest-rank hands] (highest-hands-by-rank hands)]
    (if (= 1 (count hands))
      hands
      (case highest-rank
        :high-card        (tie-hands hands)
        :one-pair         (tie-hands hands)
        :two-pair         (tie-hands hands)
        :three-of-a-kind  (tie-hands hands)
        :straight         (tie-hands hands true)
        :flush            (tie-hands hands)
        :full-house       (tie-hands hands)
        :four-of-a-kind   (tie-hands hands)
        :straight-flush   (tie-hands hands)
        "not implemented"))))
