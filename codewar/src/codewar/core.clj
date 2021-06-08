(ns codewar.core)

;; ========
;; Take a Number And Sum Its Digits Raised To The Consecutive Powers And ....¡Eureka!!
;; https://www.codewars.com/kata/5626b561280a42ecc50000d1/train/clojure

(defn exp
  "x pow n"
  [x n]
  (reduce * (repeat n x)))

(defn compute [n]
  (let [digits (map #(Character/digit % 10) (str n))]
    (apply + (map #(exp %1 (inc %2)) digits  (range)))))

(defn sum-dig-pow [a b]
  (for [x (range a (inc b) 1)
        :when (= (compute x) x)]
    x))




(comment
  (exp 2 3)
  (Math/pow 2 3)
  (range 1 11 1)

  (map #(exp %1 %2) [1 3 5] [1 2 3])
  (apply + (map #(exp %1 %2) [1 3 5] [1 2 3]))
  (map #(Character/digit % 10) (str 123))
  (compute "135")
  (compute "89")
  (compute "90")
  (str 135)
  (sum-dig-pow 1 100)
  ;;
  )
