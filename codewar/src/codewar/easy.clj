(ns codewar.easy
  (:require [clojure.string :as s]))

;; https://www.codewars.com/kata/554b4ac871d6813a03000035/train/clojure

(defn high-and-low [s]
  (let [nums (->> (s/split s #" ")
                  (map #(Integer/parseInt % 10)))]
    (str (apply max nums) " " (apply min nums))))

;; https://www.codewars.com/kata/55fd2d567d94ac3bc9000064/train/clojure
(defn row-sum-odd-numbers-too-long [row-num]
  (let [count-num-at-row (apply + (take row-num (drop 1 (range))))]
    (->> (iterate (partial + 2) 1)  ;; seq of all odd int
         (take count-num-at-row)    ;; take all int until row-num
         (reverse)
         (take row-num)             ;; take only int at row row-num
         (apply +))))

;; lig 1 -> 1 item         n
;; lig 2 -> 2 + 1 = 3      n + (n-1) = 2n -1
;; lig 3 -> 3 + 2 + 1 = 6      n + 2n -1 = 3n - 1
;; lig 4 -> 4 + 3 + 2 + 1 = 10      n + 2n -1 = 3n - 1
;; lig n -> (col n-1) + n  n + 3n -1 = 4n - 1

(comment
  (take 2 (drop 1 (range)))

  (apply + (take 2 (drop 1 (range))))

  (butlast (take 3 (iterate (partial + 2) 1)))
  (apply + (take 3 (iterate (partial + 2) 1)))

  (row-sum-odd-numbers-too-long 1)
  (row-sum-odd-numbers-too-long 2)
  (row-sum-odd-numbers-too-long 5)
  (time (row-sum-odd-numbers-too-long 2000))
  ;;
  )
;; let's try another implementation  faster than this one

;;              1
;;           3     5
;;        7     9    11
;;    13    15    17    19
;; 21    23    25    27    29
;; 31....

;; l1 l2 : +2
;; l2 l3 : +4
;; l3 l4 : +6
;; l4 l5 : +8
;; l5 l6 : +10
(defn row-sum-odd-numbers-too-long-again [row-num]
  (let [start-at (apply + (into [1] (take (dec row-num) (drop 1 (iterate (partial + 2) 0)))))]
    (apply + (take row-num (iterate (partial + 2) start-at)))))

(comment

  (def row-num 4)
  (into [1] (take row-num (drop 1 (iterate (partial + 2) 0))))
  (def start (apply + (into [1] (take (dec row-num) (drop 1 (iterate (partial + 2) 0))))))

  (take row-num (iterate (partial + 2) start))
  (take row-num (iterate (partial + 2) start))

  (row-sum-odd-numbers-too-long-again 1)
  (row-sum-odd-numbers-too-long-again 5)
  (row-sum-odd-numbers-too-long-again 7)
  (time (row-sum-odd-numbers-too-long-again 2000000))

  ;;
  )

;; let's try another implementation  faster than this one

;;              1
;;           3     5
;;        7     9    11
;;    13    15    17    19
;; 21    23    25    27    29
;; 31....

;; row 1 starts at 1
;; row 2 starts at 3
;; row 3 starts at 7
;; row 4 starts at 13
;; row 5 starts at 21
;; row 6 starts at 31
;; row n starts at (n * (n-1)) + 1
;; row n contain n items

(defn row-sum-odd-numbers [row-num]
  (let [start-from (inc (* row-num (dec row-num)))]
    (->> start-from
         (iterate (partial + 2))
         (take row-num)
         (apply +))))

(comment
  (def row-num 7)
  (let [start-from (inc (* row-num (dec row-num)))]
    (->> start-from
         (iterate (partial + 2))
         (take row-num)
         (apply +)))

  (row-sum-odd-numbers 1)
  (row-sum-odd-numbers 5)
  (row-sum-odd-numbers 7)
  (time (row-sum-odd-numbers 2000000))
  (time (row-sum-odd-numbers Integer/MAX_VALUE))
  (Integer/MAX_VALUE)
  ;;
  )

;; ok I don't understand why again, a timeout exception is thrown 
;; message posted

