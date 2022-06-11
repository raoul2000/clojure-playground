(ns leap)

(defn divisible-by-x? [x n]
  (zero? (rem n x)))

(def divisible-by-4?   (partial divisible-by-x? 4))
(def divisible-by-100? (partial divisible-by-x? 100))
(def divisible-by-400? (partial divisible-by-x? 400))

(defn leap-year? [year]
  (and (divisible-by-4? year)
       (or (not (divisible-by-100? year))
           (divisible-by-400? year))))
