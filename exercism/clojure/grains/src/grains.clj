(ns grains)

;; naive solution 

(defn square-1 [n]
  (if (= 1 n)
    1N
    (* 2 (square-1 (dec n)))))

(defn total-1 []
  (->> (range 1 65)
       (map square-1)
       (apply +)))

;; optimize it !
;; problem is that we compute values more than once
;; example: let's compute square-4
;; square-4 = square-3 * 2
;;          = (square-2 * 2) * 2
;;          = ((square-1 * 2) * 2) * 2
;; we had to compute square-3, square-2, square-1
;; to compute square-5 we must compute square-4, square-3, square-2, square-1
;; to compute square-5 ... etc ...
;; square-n = 2^(n-1)

(defn square [n]
  (reduce * (repeat  (dec n) 2N)))

;; we can also simplify total using reduce

(defn total []
  (reduce #(+ %1 (square %2)) (range 1N 65)))


