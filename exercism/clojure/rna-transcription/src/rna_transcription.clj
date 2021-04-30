(ns rna-transcription)

(defn nucleotide-dna-to-rna
  [n]
  (get {\G \C
        \C \G
        \T \A
        \A \U} n))

(defn to-rna
  "convert DNA strand to RNA strand and returns or 
   throws AssertionError on failure"
  [dna-strand]
  (let [rna (map nucleotide-dna-to-rna  dna-strand)]
    (assert (not (some nil? rna)))
    (apply str rna)))
