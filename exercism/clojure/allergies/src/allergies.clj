(ns allergies)



(def allergene-list [:eggs :peanuts :shellfish :strawberries :tomatoes :chocolate :pollen :cats])

(comment
  ;; 4 => 100
  ;; 34 => 32*1 + 2*1 => 32 2
  ;; 257 => 256*1 + 128*0 + 64*0 ....*2 + 2^0*1 = 
  ;; convert to binary
  ;; 257 => 1 0000 0001

  ;; 2^8 2^7 2^6 2^5 2^4 2^3 2^2 2^1 2^0
  ;;   8   7   6   5   4   3   2   1   0

  (map-indexed (fn [idx item]
                 (when-not (zero? (bit-and 2 (int (Math/pow 2 idx))))
                   item))  allergene-list)
  ;;
  )

(defn allergies-1 [n]
  (->> (map-indexed #(when-not (zero? (bit-and n (int (Math/pow 2 %1)))) %2) allergene-list)
       (remove nil?)))

;; using bit-test is much better than bit-and !!

(defn allergies-best-solution [n]
  (keep-indexed #(when (bit-test n %1) %2) allergene-list))

(def powers-of-2 (iterate (partial * 2) 1))

(defn allergies [n]
  (->> (map #(when-not (zero? (bit-and n %2)) %1) allergene-list powers-of-2)
       (remove nil?)))

(comment

  (map (fn [v1 v2] (vector v1 v2))
       allergene-list
       (iterate (partial * 2) 1))

  (map (fn [v1 v2] (when-not (zero? (bit-and 32 v2)) v1))
       allergene-list
       (iterate (partial * 2) 1))
  ;;
  )

(defn allergic-to? [n allergen]
  (some #{allergen} (allergies n)))
