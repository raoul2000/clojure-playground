(ns sum-of-multiples)


(defn multiple?-1 [xs]
  (fn [n]
    (some #(zero? (rem n %)) xs)))

(defn sum-of-multiples-1 [xs max]
  (->> (range 0 max)
       (filter (multiple?-1 xs))
       (apply +)))

;; refactor

;; less verbose but maybe also less readable...
(defn sum-of-multiples [xs max]
  (let [multiple? (fn [n] (some #(zero? (rem n %)) xs))]
    (reduce #(+ %1 (if (multiple? %2) %2 0)) 0 (range 0 max))))

(comment
  (sum-of-multiples [3 5] 20))

;; notes
;; Suggestion: multiplies-up-to is
;; (range n upper-bound n))
;; see https://exercism.org/tracks/clojure/exercises/sum-of-multiples/solutions/jumarko

;; using transduce
;; see https://exercism.org/tracks/clojure/exercises/sum-of-multiples/solutions/dkinzer



