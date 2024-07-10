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

(defn high-card-hand->scored-hand
  "Given a high card hand, returns a pair where the first item is a sorted list of numbers corresponding
   to card values, and the second item is the hand itself."
  ([hand]
   (high-card-hand->scored-hand hand true))
  ([hand ace-rank-low]
   [(into [] (reverse (hand-card-values hand ace-rank-low)))
    hand]))

(defn tie-high-card
  "Given a coll of hands, all with high-card, returns the coll of winner
   hands."
  [hands]
  (->> hands
       (map high-card-hand->scored-hand)
       (reduce scored-hand-reducer [])
       (map second)))


(defn one-pair-hand->scored-hand [hand]
  (->> (hand-card-values hand true)
       frequencies
       ;; group by card occurence count
       ;; 'second' is occurence count (here 2 is expected)
       (partition-by second)
       ;; flatten 1 level depth
       (mapcat identity)
       (map (fn [[card-value cnt]]
              (if (= 1 cnt)
                card-value
                (* 100 card-value))))
       (sort >)))

(comment
  
  (one-pair-hand->scored-hand "2Z 3R 5T 7R 7U")
  ;;
  )

(defn tie-one-pair [hands])

(comment

  (def c (partition-by second (frequencies (hand-card-values "2Z 3R 5T 7R 7U" true))))

  (def c2 (mapcat identity c))


  (def c3 (sort > (map (fn [[card-value cnt]]
                         (if (= 1 cnt)
                           card-value
                           (* 100 card-value))) c2)))
  c3



  (def e (map (fn [p]
                (map (fn [[card-value cnt]]
                       (if (= 1 cnt)
                         card-value
                         (* 100 card-value))) p)) c))

  (sort > (flatten e))

  ;;
  )