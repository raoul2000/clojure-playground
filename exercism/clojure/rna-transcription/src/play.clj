(ns rna-transcription)

(comment
  ((fn [s] (apply str (filter (fn [c] (Character/isUpperCase c)) s))) "Aa?bC")

  ((fn [s]
     (->> s
          (filter #(Character/isUpperCase %))
          (apply str)))
   "Aa?C")
  
  (into #{} (seq "qbcccc"))


  )