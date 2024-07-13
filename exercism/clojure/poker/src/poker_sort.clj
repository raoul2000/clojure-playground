(ns poker-sort
  (:require [clojure.string :as s]
            [poker-sort :as p]))

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

(defn highest-hands-by-rank
  "Given a list of poker hands, returns a vector where the first item is the highest
   ranks found, and the second is a coll of hands of this ranks."
  [hands]
  (->> hands
       (sort-by-rank)
       ;; partition by rank
       (partition-by first)
       ;; only consider the first one
       first
       ((juxt ffirst (partial map last)))))

(comment

  (highest-hands-by-rank ["2A 3B 6B JB 3A" ;; high card
                          "JA 10A 9A 8A 7A"
                          "JA 10A 9A 8A 7A"
                          "2A 2B 2X 2C 3A"
                          "8A 3B 6B JB 3A"])
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
            #_[:straight-flush 8 "JA 10A 9A 8A 7A"]
            [:four-of-a-kind 7 "2A 2B 2X 2C 3A"]
            [:one-pair 1 "2A 3B 6B JB 3A"]
            [:one-pair 1 "8A 3B 6B JB 3A"]))

  ((juxt ffirst (partial map last)) (first (partition-by first r1)))
  (last [1 2])

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


(defn pair-hand->scored-hand
  "Given a **one or two pair** hand, returns a pair where the first item is a sorted list of numbers corresponding
     to card values, and the second item is the hand itself."
  ([hand]
   (pair-hand->scored-hand false hand))
  ([ace-rank-low hand]
   [(->> (hand-card-values hand ace-rank-low)
         frequencies
       ;; group by card occurence count
       ;; 'second' is occurence count (here 2 is expected)
         (partition-by second)
       ;; flatten 1 level depth
         (mapcat identity)
         (map (fn [[card-value cnt]]
                (if (= 1 cnt)
                  card-value
                  (* 100  card-value))))
         (sort >)
         (into []))
    hand]))

;; TODO: function below could be refactored to handle scored hand convertion for :
;; - high cards
;; - one pair
;; - two pairs
;; - three of a kind 
;; - full house
;; - four of a kind
;; - five of a kind (not supported right now)
;; In fact it could maybe used to sort them before rank sort at step 1


(defn pair-hand->scored-hand-new
  "Given a **one or two pair** hand, returns a pair where the first item is a sorted list of numbers corresponding
     to card values, and the second item is the hand itself."
  [hand]
  [(->> (hand-card-values hand true)
        frequencies
       ;; group by card occurence count
       ;; 'second' is occurence count (here 2 is expected)
        (partition-by second)
       ;; flatten 1 level depth
        (mapcat identity)
        (map (fn [[card-value cnt]]
               (if (= 1 cnt)
                 card-value
                 ;; coef on fequencies (cnt)
                 (* (Math/pow 100 cnt)  card-value))))
        (sort >)
        (into []))
   hand])

(comment
  
  (pair-hand->scored-hand "KE KY 6E 7U 6U")
  (pair-hand->scored-hand "KE KY 2E AU AU")
  (pair-hand->scored-hand false "KE KY 2E AU AU")


  (pair-hand->scored-hand "AE AY AE 7U 6U")
  (pair-hand->scored-hand "AE AY AE 7U 7U")
  (pair-hand->scored-hand "AE AY AE AU 7U")
  ;;
  )

(defn tie-pair
  "Given a coll of hands, all with one or two pairs, returns the coll of winner
  hands."
  [hands]
  (->> hands
       (map pair-hand->scored-hand)
       (reduce scored-hand-reducer [])
       (map second)))

(comment
  (tie-pair ["2Z 4T 3F 2F 6Y" "2Z 4T 6F 9F 6Y"])
  ;;
  )

(def three-of-a-kind-hand->scored-hand pair-hand->scored-hand)
(def tie-three-of-a-kind tie-pair)

(comment
  (three-of-a-kind-hand->scored-hand "4S AH AS 8C AD")
  (->> ["2S 2H 2C 8D JH"
        "4S AH AS 8C AD"]
       (map pair-hand->scored-hand)
       #_(reduce scored-hand-reducer [])
       #_(map second))

  ;;
  )

(comment
  (->> (hand-card-values "4S AH AS 8C AD" true)
       frequencies
       (partition-by second)
       (mapcat identity)
       (map (fn [[card-value cnt]]
              (if (= 1 cnt)
                card-value
                (* 100 card-value))))
       (sort >)
       (into []))
  ;;
  )
;; main function -----------------------------------------------------

(defn best-hands [hands]
  (let [[highest-rank hands] (highest-hands-by-rank hands)]
    (if (= 1 (count hands))
      hands
      (case highest-rank
        :high-card        (tie-high-card hands)
        :one-pair         (tie-pair  hands)
        :two-pair         (tie-pair  hands)
        :three-of-a-kind  (tie-three-of-a-kind  hands)
        :straight         (tie-high-card hands)
        :flush            (tie-high-card hands)
        :full-house       (tie-high-card hands)
        "not implemented"))))

(comment
  (best-hands ["4S 6C 7S 8D 5H"
               "5S 7H 8S 9D 6H"])
  (highest-hands-by-rank ["4S 5H 4C 8D 4H"
                          "3S 4D 2S 6D 5C"])
  ;;
  )

