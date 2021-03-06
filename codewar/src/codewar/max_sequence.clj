(ns codewar.max-sequence)

;; https://www.codewars.com/kata/54521e9ec8e60bc4de000d6c/train/clojure

(defn all-partitions [xs]
  (loop [n (count xs)
         acc #{}]
    (if (= 0 n)
      acc
      (recur
       (dec n)
       (into acc (partition-all n 1 xs))))))

(defn max-sequence [xs]
  (cond
    (not-any? neg? xs) (apply + xs)
    (= 0 (count xs)) 0
    :else (first (reduce (fn [acc part]
                           (let [sum (apply + part)]
                             (if (> sum (first acc))
                               [sum part]
                               acc)))
                         [0 nil]
                         (all-partitions xs)))))





(comment
  ;; [1] => count = 1 parts = 1p1 = 1
  ;; [a b] => count = 2 parts = 2p1 + 1p2 = 3 
  ;; [a b c] => count = 3 parts = 3p1 + 2p2 + 1p3 = 6
  ;; [a b c d] => count = 4 parts = 4p1 + 3p2 + 2p3 + 1p4 = 10
  ;; (n*(n+1))/2

  (partition-all 5 1 [1 2 3 4])
  (partition-all 1 1 [1 2 3 4])
  (partition-all 2 1 [1 2 3 4])
  (= (max-sequence  [-2, 1, -3, 4, -1, 2, 1, -5, 4]) 6)
  (max-sequence [4 11 -11 39 36 10 -6 37 -10 -32 44 -26 -34 43 43])
  (max-sequence  [2, -3, 3])
  (max-sequence [-2, 1, -3, 5, -1, 2, 1, -5, 4])
  (max-sequence [2])
  (time (max-sequence [-28, -11, -4, -34, -23, -48, -75, -64, -81, -66, -66, -68, -82, -95, -102, -119, -111, -99, -118, -144, -169, -180, -151, -136, -141, -143, -138, -158, -188, -173, -143, -167, -184, -200, -221, -228, -245, -252, -272, -294, -296, -282, -310, -324, -344, -326, -300, -318, -310, -313]))

  (time (max-sequence (into [-1] (range 1 100))))
  (time (max-sequence (into [-1] (range 1 200))))
  (time (max-sequence (into [-1] (range 1 300))))
  (time (max-sequence (into [-1] (range 1 400))))
  ;;
  )
