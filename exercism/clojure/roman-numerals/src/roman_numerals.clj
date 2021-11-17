(ns roman-numerals)

(defn numerals
  "convert int value n into romans num notation.
   based on https://www.geeksforgeeks.org/converting-decimal-number-lying-between-1-to-3999-to-roman-numerals/"
  [n]
  (let [romans (into (sorted-map-by >) {1000  "M"
                                        900   "CM"
                                        500   "D"
                                        400   "CD"
                                        100   "C"
                                        90    "XC"
                                        50    "L"
                                        40    "XL"
                                        10    "X"
                                        9     "IX"
                                        5     "V"
                                        4     "IV"
                                        1     "I"})]
    (loop [num        n
           roman-seq (seq romans)
           res       []]
      (if (zero? num)
        (apply str res)
        (let [[rom-base rom-val] (first roman-seq)
              quotient           (quot num rom-base)
              remainder          (rem num rom-base)]
          (recur remainder
                 (rest roman-seq)
                 (conj res (apply str (repeat quotient rom-val)))))))))

(comment
  (numerals 1)
  (numerals 10)
  (numerals 12)
  (numerals 27)
  (numerals 1024)
  ;;
  )


