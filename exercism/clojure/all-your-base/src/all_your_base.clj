(ns all-your-base)

(defn find-smallest-pow
  "find smallest power of i lower than or equal to max.
   Example :
   ```
   (find-smallest-pow 3 42)
   => 3
   ```
   3^3 = 27 lower than 42
   3^4 = 81 NOT lower than 42
   "
  [i max]
  (last (take-while #(<= (Math/pow i %) max) (range))))

(comment
  (find-smallest-pow 3 42)
  (find-smallest-pow 3 27)
  (find-smallest-pow 2 17)
  (find-smallest-pow 10 1)
  ;;
  )

(defn powers-of [i max]
  (->> (find-smallest-pow i max)
       inc
       range
       reverse))

(comment
  (powers-of 2 420)
  (powers-of 10 5)
  ;;
  )

(defn base-10->base-n [i base]
  (loop [num    i
         powers (powers-of base i)
         res    []]
    (if (empty? powers)
      res
      (let [power (first powers)
            div   (int (Math/pow base power))]
        (recur (rem num div)
               (rest powers)
               (conj res (quot num div)))))))

(comment
  (base-10->base-n 42 3)
  (base-10->base-n 1 2)
  (base-10->base-n 2 2)
  (base-10->base-n 3 2)
  (base-10->base-n 40 10)
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
  ;;
  )


(defn convert [base val to-base]
  (cond
    (or (< base 2 )
        (< to-base 2)) nil
    (empty? val)       val
    (every? zero? val) [0]
    (some neg? val)    nil
    (not-every? (partial > base)  val)  nil
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

