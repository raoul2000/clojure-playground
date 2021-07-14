(ns codewar.product-of-fib)

;; https://www.codewars.com/kata/5541f58a944b85ce6d00006a/train/clojure




(defn fib
  ([]
   (fib 1N 1N))
  ([a b]
   (lazy-seq (cons a (fib b (+ a b))))))

(comment

  (time (last (take 3000 (fib))))

  ;;
  )
(defn product-fib [prod]
  (loop [fibs (fib)]
    (let [a (first fibs)
          b (ffirst fibs)]
      (if (> (+ a b) prod)
        [a b]
        (recur (rest fibs))))))

(comment
  (product-fib 50)
  ;;
  )

(defn lz []
  (let [n (range 1 20)]
    (prn n)
    n))
(comment
  (take 10 (lazy-seq (lz)))
  (take 10 (lz))





;;
  )