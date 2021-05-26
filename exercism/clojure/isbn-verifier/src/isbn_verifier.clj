(ns isbn-verifier)

(defn isbn? [isbn] ;; <- arglist goes here
  (if (re-matches #"(?:(\d+)\-?)+[X\d]$" isbn)
    (= 0 (mod
          (->> isbn
               reverse
               (filter #(or
                         (Character/isDigit %)
                         (= \X %)))
               (into [])
               (reduce-kv (fn [r k v]
                            (+ r (* (inc k) (if (= \X v)
                                              10
                                              (Character/digit v 10))))) 0)) 11))
    false))
