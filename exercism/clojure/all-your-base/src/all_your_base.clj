(ns all-your-base)

(defn find-smallest-pow
  "find smallest power of i lower than or equal to max."
  [i max]
  (last (take-while #(<= (Math/pow i %) max) (range))))

(comment
  (= 3 (find-smallest-pow 3 42))
  (= 3 (find-smallest-pow 3 27))
  (= 4 (find-smallest-pow 2 17))
  (= 0 (find-smallest-pow 10 1))
  ;;
  )

(defn powers-of
  "returns seq of powers of i to combine to obtain max."
  [i max]
  (->> (find-smallest-pow i max)
       inc
       range
       reverse))

(comment
  (powers-of 2 420)
  (powers-of 10 5)
  ;;
  )


(defn base-10->base-n [i to-base]
  (if (zero? i)
    '(0)
    (loop [num i
           result '()]
      (if (zero? num)
        result
        (recur (quot num to-base)
               (conj result (rem num to-base)))))))


(comment
  (base-10->base-n 42 3)
  (base-10->base-n 1 2)
  (base-10->base-n 2 2)
  (base-10->base-n 3 2)
  (base-10->base-n 40 10)
  (base-10->base-n 0 10)
  ;;
  )

(defn base-n->base-10 [base-n val]
  (reduce-kv
   (fn [acc k c]
     (+ acc (* c  (int (Math/pow base-n k)))))
   0
   (apply vector (reverse val))))

(comment
  (base-n->base-10  2 '(1 0 1 0 1 0))
  (base-n->base-10  10 '(4 2))
  (base-n->base-10  10 '(5))
  (base-n->base-10  3 '(1 1 2 0))
  (base-n->base-10  2 '(1))
  (base-n->base-10  4 '(0 0 0))
  (base-n->base-10  4 '())
  ;;
  )

(defn convert [base val to-base]
  (cond
    (or (< base 2)
        (< to-base 2)
        (some neg? val)
        (not-every? (partial > base)  val)) nil    
    (empty? val)                            val
    :else (-> (base-n->base-10 base val)
              (base-10->base-n to-base))))


(comment
  (convert 2 '(1) 10)
  (convert 2 '(1 0 1) 10)
  (convert 10 '(5) 2)
  (convert 2 '(1 0 1 0 1 0) 10)
  (convert 10 '(4 2) 2)
  (convert 3 '(1 1 2 0) 16)
  (convert 16 '(2 10) 3)
  (convert 97 '(3 46 60) 73)
  (convert 2 () 10)
  (convert 10 '(0) 2)
  (convert 10 '(0 0 0) 2)
  (convert 7 '(0 6 0) 10)
  (convert 2 '(1 -1 1 0 1 0) 10)
  (convert 2 '(1 2 1 0 1 0) 10)



  (base-n->base-10 2 '(1))
  (base-10->base-n 1 10)
  (base-10->base-n 1 10)
  ;;
  )

(defn decimal-n-to-base-x [n x]
  (if (== n 0)
    '(0)
    (loop [num n
           result '()]
      (if (== num 0)
        result
        (recur (quot num x) (conj result (rem num x)))))))

(comment
  (decimal-n-to-base-x 42 2)
  (decimal-n-to-base-x 42 3)


  ;;
  )

