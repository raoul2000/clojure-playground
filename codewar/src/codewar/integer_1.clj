(ns codewar.integer-1)



;; https://www.codewars.com/kata/55aa075506463dac6600010d/clojure

(defn square-root-int? [n]
  (zero? (rem (Math/sqrt n) 1)))

(defn divisors [n]
  (filter #(= 0 (rem n %)) (range 1 (inc n))))

(defn map-square [d]
  (map #(* % %) d))

(defn sum-square-div [n]
  (let [sum-divisors (apply + (map-square (divisors n)))]
    (when (square-root-int? sum-divisors) sum-divisors)))

(defn list-squared [m n]
  (for [i     (range m (inc n))
        :let  [sum (sum-square-div i)]
        :when sum]
    [i  sum]))

(comment
  (range 1 (inc 4))
  (rem 5 2)
  (rem 246 82)
  (filter #(= 0 (rem 246 %)) (range 1 (inc 246)))
  (divisors 42)
  (sum-square-div 42)
  (sum-square-div 4)
  (apply + (map-square (divisors 42)))

  (list-squared 1 250)
  (square-root-int? 84100)
  (square-root-int? 2500)

  ;;
  )