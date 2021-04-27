(ns rna-transcription)

(defn nucleotide-dna-to-rna
  "convert DNA nucleotide n to RNA nucleotide or throws 
  when not possible"
  [n]
  (or
   (get {\G \C
         \C \G
         \T \A
         \A \U} n)
   (throw (AssertionError. (str "invalid nucleotide : " n)))))

(defn to-rna
  "convert DNA strand to RNA strand"
  [dna-strand]
  (->> (map nucleotide-dna-to-rna  dna-strand)
       (apply str)))
