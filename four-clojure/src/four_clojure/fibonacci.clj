(ns four-clojure.fibonacci
  (:gen-class))

;; Write a function which returns the first X fibonacci numbers.

(defn fib
  [n]
  (loop [s (range 0 n)
         result []]
    (if (empty? s)
      result
      (recur (rest s)
             (cond
               (empty? result) [1]
               (= 1 (count result)) [1 1]
               :else (conj result (apply + (take-last 2 result))))))))

(comment
  (fib 4)
  (fib 6)
  (fib 8)
  (range 0 5))
(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
