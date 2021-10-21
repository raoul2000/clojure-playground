(ns raindrops)

(defn convert-1 [num]
  (cond
    (zero? (mod num 105)) "PlingPlangPlong"
    (zero? (mod num 15))  "PlingPlang"
    (zero? (mod num 21))  "PlingPlong"
    (zero? (mod num 35))  "PlangPlong"
    (zero? (mod num 3))   "Pling"
    (zero? (mod num 5))   "Plang"
    (zero? (mod num 7))   "Plong"
    :else (str num)))

(defn convert-2 [n]
  (let [str-result (reduce (fn [acc [divisor word]]
                             (str acc (when
                                       (zero? (mod n divisor))
                                        word))) "" [[3 "Pling"]
                                                    [5 "Plang"]
                                                    [7 "Plong"]])]
    (if  (empty? str-result)
      (str n)
      str-result)))

(defn convert [n]
  (cond-> nil
    (zero? (mod n 3)) (str "Pling")
    (zero? (mod n 5)) (str "Plang")
    (zero? (mod n 7)) (str "Plong")
    :always (or (str n))))


