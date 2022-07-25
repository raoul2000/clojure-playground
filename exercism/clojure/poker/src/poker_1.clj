(ns poker-1
  (:require [clojure.string :refer [split join]]))


(def figure-vals {"J" 11
                  "Q" 12
                  "K" 13})

(defn normalize-card [card]
  (let [[_ rank suit] (re-matches #"([0-9JQK]+)(.)" card)]
    [(or (figure-vals rank)
         (Integer. rank))
     suit]))

(comment
  (normalize-card "3H")
  (normalize-card "JH")
  (normalize-card "KH")
  (normalize-card "10H")
  ;;
  )

(defn sort-by-rank-desc [hand]
  (reverse (sort-by first hand)))

(defn normalize-hand
  "Given a string representing a list of cards, returns a vector sorted
   by card rank where each card is represented a a pair [rank suit]"
  [s]
  (->> (split s #" ")
       (map normalize-card)
       (sort-by-rank-desc)))

(comment
  (normalize-hand "4S 3D")
  (normalize-hand "JE 4S 3D KH")
  ;;
  )

;; some helper functions
(def card-rank first)
(def card-suit last)

;; 

(def prime-val {2 2
                3 3
                4 5
                5 7
                6 9
                7 11
                8 13
                9 17
                })
(defn score-highest-cards [hand]
  (apply + (map #(* %2 (card-rank  %1)) (reverse hand) (range 1 (inc (count hand))))))

(comment
  (score-highest-cards [[4 "D"] [2 "_"]])
  (score-highest-cards [[4 "D"] [2 "_"] [10 "_"]])
  (score-highest-cards [[5 "D"] [1 "_"] [2 "_"]])

  )

(defn highest-cards-0 [hands]
  (map #(vector (score-highest-cards %) %) hands))

(defn highest-card-rank [hand]
  (->> hand
       (map first)
       (apply max)))

(comment
  (highest-card-rank (normalize-hand "4D 5S 6S 8D 3C"))
  (highest-card-rank (normalize-hand "4D QS 6S 8D KC")))

(defn highest-cards [hands]
  (reduce (fn [winners hand]
            (if (or (empty? winners)
                    (>= (ffirst hand) (first (ffirst winners))))
              (conj winners hand)
              winners))
          ()
          hands))

(defn compare-hands-by-rank [h1 h2]
  (loop [v1 (sort h1)
         v2 (sort h2)]
    (if (empty? v1)
      0
      (cond
        (< (first v1) (first v2))  -1
        (> (first v1) (first v2))  1
        :else                     (recur (rest v1) (rest v2))))))

(defn select-highest [hands]
  (let [sorted-hands  (sort compare-hands-by-rank hands)
        first-highest (first sorted-hands)]
    (into [first-highest] (take-while #(= % first-highest) (rest sorted-hands)))))


(comment
  (def h1 [5 4 2])
  (def h2 [5 4 2])

  (select-highest [[1 5 4]
                   [2 3 5]
                   [1 4 5]])
  (reduce
   (sort compare-hands-by-rank [h1 h2]))
  ()
  (loop [v1 h1
         v2 h2]
    (if (empty? v1)
      0
      (cond
        (< (first v1) (first v2))  -1
        (> (first v1) (first v2))  1
        :else                     (recur (rest v1) (rest v2)))))

  ;;
  )

(def hand-1 ["4D 5S 6S 8D 3C"
             "2S 4C 7S 9H 10H"
             "3S 4S 5D 6H JH"])

(comment
  (highest-cards (map normalize-hand hand-1))

  ;;
  )

(defn best-hands [hands] ;; <- arglist goes here
  (if (= 1 (count hands))
    hands
    (cond)))
