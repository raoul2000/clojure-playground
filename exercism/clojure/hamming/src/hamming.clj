(ns hamming)

(defn distance [strand1 strand2]
  (if (not= (count strand1) (count strand2))
    nil
    (count
     (filter #(not (zero? %1))
             (map #(- (int %1) (int %2)) strand1 strand2)))))

