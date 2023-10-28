(ns codewar.unique-order)

(defn unique-in-order [input]
  (->> (partition-by identity input)
       (map first)))

;; use dedupe
;; (dedupe "aaabcccdee")
;;remove all consecutive identical items
;; to remove all duplicate items even not consecutive, use 'distinct'


