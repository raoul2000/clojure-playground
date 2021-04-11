(ns nucleotide-count)

(defn count-of-nucleotide-in-strand [nucleotide strand]
  (def result (reduce
               #(+ %1 (if-not (= %2 nucleotide) 0 1))
               0
               (seq strand)))
    (if (= 0 result)
      (throw (Throwable.))
      result))


(defn nucleotide-counts [strand] ;; <- Arglist goes here
  ;; your code goes here
  )


(comment
  (def total [(+ 1 2)]
    (println total))
  (count-of-nucleotide-in-strand \C "eeCse")
  (reduce
   #(+ %1 (if-not (= %2 \C) 0 1))
   0
   (seq "CCCCC"))
  (+ 1 (if true 1 0))
  (seq "ABC"))
