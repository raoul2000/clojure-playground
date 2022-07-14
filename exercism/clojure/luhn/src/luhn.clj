(ns luhn)



(comment
  (remove neg-int? (map #(Character/digit % 10) " 1 2 5 4"))

  (->> "4539 3195 0343 6467"
       (map #(Character/digit % 10))
       (remove neg-int?)
       (reverse)
       (map-indexed #(if (odd? %1) (* 2 %2) %2))
       (map #(if (> %1 9) (- %1 9) %1))
       (apply +))



  (->> " 5 9 "
       (map #(Character/digit % 10))
       (reverse)
       (into [])
       (map-indexed #(if (and (pos? %2) (odd? %1))
                       (* 2 %2)
                       %2))
       (map #(if (> %1 9) (- %1 9) %1))
       (remove neg?)
       (apply +))

  ;;
  )

(defn sum-of-digits [digit-xs]
  (->> digit-xs
       (reverse)
       (into [])
       (map-indexed #(if (and (pos? %2) (odd? %1))
                       (* 2 %2)
                       %2))
       (map #(if (> %1 9) (- %1 9) %1))
       (remove neg?)
       (apply +)))

(comment
  (sum-of-digits '(5 9))
  ;;
  )


(defn valid? [s] ;; <- arglist goes here
  (and (boolean (re-matches #"[0-9 ]+" s))
       (let [digit-xs (remove neg-int? (map #(Character/digit % 10) s))]
         (if (= 1 (count digit-xs))
           false
           (zero? (rem (sum-of-digits digit-xs) 10))))))

(comment
  (valid? "046a 454 286")

  ;;
  )