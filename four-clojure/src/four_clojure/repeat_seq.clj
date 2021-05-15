(ns four-clojure.repeat-seq)

;; Write a function which replicates each element of a sequence a variable number of times.
;; https://www.4clojure.com/problem/33

(defn my-rep
  [s n]
  (reduce #(into %1 (repeat n %2)) [] s))

(defn intrp
  [e c]
  (rest (reduce #(conj %1 e %2) [] c)))

(defn drop-every-nth 
  [coll n]
  (flatten (map #(if (< (count %) n)
          %
          (drop-last  %)) (partition-all n coll))))

(comment
  (my-rep [1 2 3] 2)
  (my-rep [[1 2] [3 4]] 2)
  (fn [m] (map #(if (= 1 (count %)) (first %) %) (partition-by identity m)))

  (intrp 0 [1 2 3])
  (apply str (intrp ", " ["one" "two" "three"]))

  (drop-every-nth [1] 3)
  (drop-every-nth [1 2 ] 3)
  (drop-every-nth [1 2 3 4 5 6 7 8] 3)
  (drop-every-nth [:a :b :c :d :e :f] 2)
  )