(ns codewar.integer-1)



;; https://www.codewars.com/kata/55aa075506463dac6600010d/clojure

(defn square-root-int?
  "returns true if the square root of n is an int"
  [n]
  (let [int-part (.intValue (BigDecimal/valueOf (Math/sqrt n)))]
    (= n (* int-part int-part))))

(defn divisors [n]
  (filter #(= 0 (rem n %)) (range 1 (inc n))))

(defn square [d]
  (map #(* % %) d))

(defn sum-square-div [n]
  (let [sum-divisors (apply + (square (divisors n)))]
    [(square-root-int? sum-divisors) sum-divisors]))

(defn list-squared [m n]
  (reduce (fn [acc item]
            (let [res (sum-square-div item)]
              (if (first res)
                (conj acc [item (second res)])
                acc)))
          []
          (range m (inc n))))


(comment
  (range 1 (inc 4))
  (rem 5 2)
  (rem 246 82)
  (filter #(= 0 (rem 246 %)) (range 1 (inc 246)))
  (divisors 42)
  (sum-square-div 42)
  (sum-square-div 4)
  (apply + (square (divisors 42)))

  (list-squared 1 250)
  (square? 84100)
  (square? 2500)

  ;;
  )