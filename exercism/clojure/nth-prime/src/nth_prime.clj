(ns nth-prime)


(defn prm [start size prime-init]
  (loop [r (range start (+ start size))
         prime prime-init]
    (let [divisor (first r)]
      (if (= 1 (count r))
        (conj prime divisor)
        (recur
         (remove #(zero? (rem % divisor)) r)
         (conj prime divisor))))))

(comment

  (prm 2 9 [])
  (prm 8 9 [2 3 5 7])

  (time (loop [r (range 2 9)
               prime []]
          (let [divisor (first r)]
            (if (= 1 (count r))
              (conj prime divisor)
              (recur
               (remove #(zero? (rem % divisor)) r)
               (conj prime divisor))))))
  ;;
  )




(def  multiple-of-2?      even?)
(defn multiple-of-3? [n] (zero? (rem n 3)))
(defn multiple-of-5? [n] (zero? (rem n 5)))


;; (take 5 (reduce (fn [acc n] (conj acc (+ (* 6 n) 1))) [] (range 1 20)))

(defn prime-pair [n]
  [(- (* 6 n) 1)
   (+ (* 6 n) 1)])

(comment
  (prime-pair 100)
  (prime-pair 4)

  ;;
  )
(defn prime-seq []
  (flatten
   (map prime-pair (iterate inc 1))))

(comment
  (take 50 (prime-seq))
  ;;
  )
(defn nth-prime-1 [n] ;; <- arglist goes here
  (if (zero? n)
    (throw (IllegalArgumentException.))
    (get
     (into [2 3] (take (inc n) (prime-seq)))
     (dec n))))

(comment
  (nth-prime-1 1)
  (nth-prime-1 2)
  (nth-prime-1 6)
  (nth-prime-1 50)
  (nth-prime-1 0)
  (for [n (range 1 10)]
    [(- (* 6 n) 1) (+ (* 6 n) 1)])

  (take 5
        (flatten
         (map #(vector (- (* 6 %) 1) (+ (* 6 %) 1)) (iterate inc 1))))

  (time (last
         (into [2 3] (take 50
                           (flatten
                            (map prime-pair (iterate inc 1)))))))

  (time (get
         (into [2 3] (take 10001 (prime-seq)))
         10001))

  (last (take 100 (prime-seq)))
  (get [2 3 4] 0)
  ;;
  )




(defn prime? [n prime-seq]
  (every? #(pos? (rem n %)) prime-seq))

(comment
  (prime? 3 [2])
  (prime? 4 [2 3])
  (prime? 5 [2 3])
  (prime? 7 [2 3 5])
  (prime? 8 [2 3 5 7])
  (prime? 11 [2 3 5 7])
  (prime? 12 [2 3 5 7 11])
  ;;
  )


(defn next-prime [prev-primes]
  (inc (last (take-while #(not (prime? % prev-primes))
                         (drop (inc (count prev-primes)) (range))))))

(defn next-prime-1 [prev-primes]
  (inc (last (take-while #(not (prime? % prev-primes))
                         (iterate inc (count prev-primes))))))

(defn next-prime-2 [prev-primes]
  (->> (iterate inc (count prev-primes))
       (take-while #(not (prime? % prev-primes)))
       last
       inc))

(comment
  (next-prime [2])
  (next-prime [2 3 5])
  (next-prime [2 3 5 7])
  (next-prime [2 3 5 7 11])
  (next-prime [2 3 5 7 11 13])
  (next-prime [2 3 5 7 11 13 17])
  (next-prime [2 3 5 7 11 13 17 19])
  (next-prime [2 3 5 7 11 13 17 19 23])
  (next-prime [2 3 5 7 11 13 17 19 23 29])
  (next-prime [2 3 5 7 11 13 17 19 23 29 31])
  (time (next-prime [2 3 5 7 11 13 17 19 23 29 31 37]))
  ;;
  )

(defn nth-prime [n] ;; <- arglist goes here
  (if (zero? n)
    (throw IllegalArgumentException)
    (loop [prime [2]]
      (if (= (count prime) n)
        (last prime)
        (recur (conj prime (next-prime prime)))))))

(comment
  (nth-prime 1)
  (nth-prime 2)
  (nth-prime 6)
  (time (nth-prime 6))
  (time (nth-prime 40))
  (time (nth-prime 100))
  (time (nth-prime 200))
  (time (nth-prime 300))
  ;;(nth-prime 10001)
  ;;
  )