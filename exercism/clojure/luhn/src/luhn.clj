(ns luhn)


(defn- double-digit-and-adjust [i]
  (cond-> (* 2 i)
    (> i 4) (- 9)))


(defn- transform-digit-at-index [index i]
  (if (odd? index) (double-digit-and-adjust i) i))

(defn- sum-of-digits [digit-xs]
  (->> digit-xs
       ((comp (partial into []) reverse))
       (map-indexed transform-digit-at-index)
       (apply +)))

(defn- parse-digit-xs [s]
  (remove neg-int? (map #(Character/digit % 10) s)))

(defn valid? [s]
  (boolean (and  (re-matches #"[0-9 ]+" s)
                 (let [digit-xs (parse-digit-xs s)]
                   (when (> (count digit-xs) 1)
                     (zero? (rem (sum-of-digits digit-xs) 10)))))))
