(ns prime-factors)

;; heading in the wrong direction below
;; working finding if a number is a prime number !
;; no no no

(defn divides? [n d]
  (zero? (rem n d)))

(defn has-divisor? [n]
  (let [divised-n? (partial divides? n)]
    (boolean (some divised-n? (range 2 (dec n)))))) ;; replace (dec n) with (quot n 2) ?

(defn prime-number? [n]
  (cond
    (or (= 2 n)
        (= 3 n))    true
    (or (<= n 1)
        (zero? (rem n 2))
        (zero? (rem n 3)))   false
    :else (not (has-divisor? n))))

(comment
  (divides?  16 5)
  (has-divisor? 11)
  (has-divisor? 13)
  (has-divisor? 14)
  (filter prime-number? (range 1 100))
  (filter prime-number? (range 1 20))

  (time (last (->> (range 1 10000)
                   (remove has-divisor?))))
  ;; 
  )

;; actually we don't need to calculate prime number
;; to get the prime factor, just apply the algo

(defn divisor? [d n]
  (zero? (rem n d)))

(comment
  (divisor? 2 16))

(defn of-1 [n]
  (loop [remainder n
         divisor   2
         result    []]
    (if (= 1 remainder)
      result
      (if (divisor? divisor remainder)
        (recur (quot remainder divisor)
               divisor
               (conj result divisor))
        (recur remainder
               (inc divisor)
               result)))))

(defn of [n]
  (loop [remainder        n
         factor           2
         prime-factors    []]
    (cond
      (= 1 remainder)              prime-factors
      (divisor? factor remainder) (recur (quot remainder factor)
                                          factor
                                          (conj prime-factors factor))
      :else                        (recur remainder
                                          (inc factor)
                                          prime-factors))))

(comment
  (of 4)
  (of 5)
  (of 6)
  (of 8)
  (of 9)
  (of 27)
  (of 625)
  (of 901255)
  (of 93819012551)
  (of 20)
  ;;
  )