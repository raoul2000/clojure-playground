(ns reverse-string)

(defn reverse-string [s]
  (reduce str "" (into () (vec (seq s)))))
