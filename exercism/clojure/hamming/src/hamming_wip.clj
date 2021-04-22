(ns hamming)

(defn distance [strand1 strand2] ; <- arglist goes here
  (if (= (count strand1) (count strand2))
    (count
     (filter #(not (zero? %1))
             (map #(- (int %1) (int %2)) strand1 strand2)))))

(comment
  (map #(- %1 %2)  '(1 2) '(3 2))
  (count
   (filter #(not (zero? %1))
           (map #(- (int %1) (int %2)) "ACT" "GGA"))))
