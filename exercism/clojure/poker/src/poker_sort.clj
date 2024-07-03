(ns poker-sort
  (:require [clojure.string :as s]))

;; This is an attempt to solve the Poker exercism by defining a sort order
;; on all hands

(defn card-value [^String s ace-start]
  (let [[_ value] (re-matches #"^(.+).$" s)]
    (case value
      "K"  13
      "Q"  12
      "J"  11
      "A"  (if ace-start 1 14)
      (Integer/parseInt value))))

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
  (let [cards (s/split hand #" ")
        ace-start (->> cards
                       (map #(card-value % true))
                       sort)
        ace-end   (->> cards
                       (map #(card-value % false))
                       sort)]
    (or (consecutive-values? ace-start)
        (consecutive-values? ace-end))))



(comment
  (def h1 "2Z 3E 4R AE")
  (def h "KR AE")
  (->> (s/split h #" ")
       (map #(card-value % false)))

  (defn consecutive-values? [coll]
    (= #{1} (into #{} (map (fn [[a b]] (- b a)) (partition 2 1 coll)))))

  (let [cards (s/split h #" ")
        ace-start (->> cards
                       (map #(card-value % true))
                       sort)
        ace-end (->> cards
                     (map #(card-value % false))
                     sort)]
    [ace-start
     (consecutive-values? ace-start)
     ace-end
     (consecutive-values? ace-end)])

  (range 1 5)
  (next [1 2 3])
  (map - (next [1 2 3]) [1 2 4])
  (= #{1} (into #{} (map (fn [[a b]] (- b a)) (partition 2 1 [1 2 3 4 5]))))
  ;;
  )








  