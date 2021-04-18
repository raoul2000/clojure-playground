(ns nucleotide-count)

(defn count-of-nucleotide-in-strand [nucleotide strand]
  (if (some #(= % nucleotide) [\T \C \G \A])
    (reduce #(+ %1 (if-not (= %2 nucleotide) 0 1)) 0 strand)
    (throw (Throwable.))))


(defn nucleotide-counts [strand]
  (reduce conj {}
          (map #(vector % (count-of-nucleotide-in-strand % strand)) [\A \T \C \G])))
