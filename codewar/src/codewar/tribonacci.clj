(ns codewar.tribonacci)

(defn tribonacci_1 [xs n]
  (if (zero? n)
    []
    (loop [result xs]
      (if (> (count result) n)
        (take n result)
        (recur (conj result (apply + (take-last 3 result))))))))

(defn tribonacci [xs n]
  (cond
    (zero? n)        []
    (> (count xs) n) (take n xs)
    :else            (loop [result xs]
                       (if (= (count result) n)
                         result
                         (recur (conj result (apply + (take-last 3 result))))))))

(comment
  (tribonacci  [1 1 1] 10)
  (tribonacci  [0.5 0.5 0.5] 30)
  (tribonacci  [1 1 1] 1)
  (tribonacci  [300 200 100] 0)

  ;;
  )


