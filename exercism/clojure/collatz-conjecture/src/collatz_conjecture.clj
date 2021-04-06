(ns collatz-conjecture)


(defn compute-next [num]
  (if (even? num)
    (/ num 2)
    (+ 1 (* 3 num))))

(defn collatz [num] ;; <- arglist goes here
  (if (<=  num 0)
    (throw (Throwable.))
    (loop [iteration 0
           n num]
      (if
       (= n 1) iteration
       (recur
        (inc iteration)
        (compute-next n))))))



(comment
  (or false 1 3)
  (and true true 3)
  (do (if true "true"))
  (loop [s "s"
         it 0]
    (println s)
    (if (= 3 (count s))
      "done"
      (recur (str s "s") (inc it))))
  (odd? 0)
  (compute-next 5)
  (even? 0)
  (collatz 0)
  (collatz 1)
  
  )