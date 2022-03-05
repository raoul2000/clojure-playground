(ns fib)

(defn fibo-1 [n]
  (cond
    (< n 2) n
    :else  (+ (fibo-1 (dec n))
              (fibo-1 (- n 2)))))

(fn [c]
  (defn fn1 [n] n)
  (fn1 2))


(defn fibo
  ([n]
   (if (< n 2)
     1
     (fibo (dec n) 1 1)))
  ([n a b]
   (if (zero? n)
     b
     (recur (dec n) b (+ a b)))))

