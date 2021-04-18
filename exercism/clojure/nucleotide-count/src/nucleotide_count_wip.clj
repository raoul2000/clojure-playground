(ns nucleotide-count)

(defn count-of-nucleotide-in-strand
  "returns count of nucleotide in strand or throw is invalid nucleotide"
  [nucleotide strand]
  (if (some #(= % nucleotide) [\T \C \G \A])
    (reduce
     #(+ %1 (if-not (= %2 nucleotide) 0 1))
     0
     strand)
    (throw (Throwable.))))


(defn nucleotide-counts [strand] ;; <- Arglist goes here
  (reduce conj {}
          (map #(vector % (nucleotide-count/count-of-nucleotide-in-strand % strand)) [\A \T \C \G])))


(comment
  (reduce conj {}
          (map #(vector % (nucleotide-count/count-of-nucleotide-in-strand % "ATCTAC")) [\C \T]))
  (map #(vector % (nucleotide-count/count-of-nucleotide-in-strand % "ATCTAC")) [\C \T])
  (reduce conj {} [[:a 1]])
  (reduce #(assoc %1 %2 (nucleotide-count/count-of-nucleotide-in-strand %1 "ATCG")) {} [\A \T \C \G])
  (map #(inc %) {1 2})
  (map inc [1 2])
  (map inc #{1 2})
  (map #(inc (last %)) {:a 1 :b 2})
  (map inc [1 2])

  (nucleotide-count/nucleotide-counts "ATTGAGT")
  (def total [(+ 1 2)]
    (println total))
  (count-of-nucleotide-in-strand \X "eeC C Cse")
  (reduce
   #(+ %1 (if-not (= %2 \C) 0 1))
   0
   (seq "CCCCC"))
  (+ 1 (if true 1 0))
  (seq "ABC"))
