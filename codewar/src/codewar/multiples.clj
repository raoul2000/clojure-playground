(ns codewar.multiples)

;; https://www.codewars.com/kata/514b92a657cdc65150000006/train/clojure

(defn multiple-of-3-or-5? [n]
  (some zero? [(rem n 3) (rem n 5)]))

(defn solution [number]
  (->> (range number)
       (reduce #(if (some zero? [(rem %2 3) (rem %2 5)])
                  (+ %1 %2)
                  %1) 0)))


(comment
  ;; all natural numbers below 10
  (range 1 (inc 10))

  ;; denominator (3) is a multiple of nominator (9) is the remainder of the
  ;; division is zero
  (rem 9 3)

  ;; reduce
  (reduce #(+ %1 (if (or
                      (zero? (rem %2 3))
                      (zero? (rem %2 5)))
                   %2
                   0)) 0 (range 1 10))

  (solution 10)
  ;;
  )