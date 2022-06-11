(ns perfect-numbers)

;; 1. find all factors of a given number 
;; 2. remove the number itself
;; 3. calculate the sum of factors
;; 4. compare and categorize

(defn factor?
  "Returns true if d is a factor of n"
  [n d]
  (zero? (rem n d)))

(defn all-factors-of
  "Returns a list of all divisors of n except n itself"
  [n]
  (filter (partial factor? n) (range 1 n)))

(defn classify [n]
  (when (neg-int? n)
    (throw (IllegalArgumentException.)))
  (let [aliquot-sum (apply + (all-factors-of n))]
    (cond
      (= aliquot-sum n) :perfect
      (> aliquot-sum n) :abundant
      :else             :deficient)))



(comment

  (apply + (all-factors-of 28)))

