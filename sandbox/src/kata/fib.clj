(ns kata.fib)

;; fibonacci
;; f(0) = 0
;; f(1) = 1
;; f(2) = f(1) + f(0) = 0 + 1 = 1
;; f(3) = f(2) + f(1) = 1 + 1 = 2
;; etc...
;; when n >= 2  f(n) = f(n-1) + f(n-2

;; first implementation
(defn fib-1 [n]
  (cond
    (zero? n)  0
    (= 1 n)    1
    :else (+ (fib-1 (dec n))
             (fib-1 (- n 2)))))

;; Implementation above is ok, and works fine ... when n is 'not too big'.
(time (fib-1 10))     ;; "Elapsed time: 0.107899 msecs"
;(time (fib-1 15))     ;; "Elapsed time: 0.9397 msecs"
;(time (fib-1 20))     ;; "Elapsed time: 1.957501 msecs"
;(time (fib-1 30))     ;; "Elapsed time: 13.487499 msecs"
;(time (fib-1 40))     ;; "Elapsed time: 1317.8032 msecs"
;(time (fib-1 43))     ;; "Elapsed time: 6076.7365 msecs"

;; (fib 43) take more than 6s when (fib 40) took 1.3s
;; Let's see how we can compute fib n with n > 43 in a non too long time

;; to avoid calculating all fib n-... let's try to use memoize
;; A memoized function is a function that caches the results related to a given arg
;; see https://clojuredocs.org/clojure.core/memoize

(def fib-2 (memoize (fn [n]
                      (cond
                        (zero? n)  0
                        (= 1 n)    1
                        :else (+ (fib-2 (dec n))
                                 (fib-2 (- n 2)))))))

;(time (fib-2 43)) ;; "Elapsed time: 0.0271 msecs"
;; this is in fact muuuch better
;(time (fib-2 50)) ;; "Elapsed time: 0.0912 msecs

;;(time (fib-2 100)) ;; this one throws an exception "long overflow"

;; let's try to see how we can solve this

(def fib-3 (memoize (fn [n]
                      (cond
                        (zero? n)  0N
                        (= 1 n)    1N
                        :else (+ (fib-3 (dec n))
                                 (fib-3 (- n 2)))))))

;(time (fib-3 100))
;; "Elapsed time: 0.2295 msecs"
;; 354224848179261915075N
;; well that's a big number !! and the processing time is still pretty low
;(time (fib-3 300))
;; "Elapsed time: 0.0294 msecs"
;; 222232244629420445529739893461909967206666939096499764990979600N
;; ok stop here.

;; could it be possible to get the same performance without memoize ?
;; If we can access to the value of fib(n-1) and fib(n-2) we can compute fib(n) right ?
;; In the initial version, what took so long was that for each 'n' we had to compute all
;; preceeding fibs. 
;; what if we could store them in a vector and add the 2 last values
;;
;; The vector is initialized with [0 1]
;; fib(2) => [0 1 1]
;; fib(3) => [    1 2]
;; fib(4) => [      2 3]
;; fib(5) => [        3 5]
;; fib(6) => [          5 8]

(defn fib [n]
  (cond
    (zero? n)  0
    (= 1 n)    1
    :else      (loop [i    1
                      prev [0 1N]]   ;; initial values for fib(0) and fib(1)
                 (if (= i n)
                   (last prev)
                   (recur (inc i)
                          (into [] (rest (conj prev (apply + prev)))))))))

;(time (fib 30))     ;; "Elapsed time: 13.487499 msecs"
;(time (fib 40))     ;;  2.7268 msecs (fib-1 :  1317.8032)
;(time (fib 100))     
;; 354224848179261915075N
;;  3.3597 msecs (fib-1 : +++, fib-2 : 0.2295 msecs)

;(time (fib 300))
;; 12.2836 msecs (fib-2 : 0.0294 msecs)
;; 222232244629420445529739893461909967206666939096499764990979600N

;; this is much better !!
