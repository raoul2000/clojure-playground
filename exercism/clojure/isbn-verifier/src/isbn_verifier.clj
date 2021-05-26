(ns isbn-verifier)

(defn isbn-char-to-int
  [c]
  (if (= \X c)
    10
    (Character/digit c 10)))

(defn isbn-checksum [r k v]
  (+ r (* (inc k) (isbn-char-to-int v))))

(defn isbn? [isbn]
  (let [no-dash  (filter #(not (= \- %)) isbn)
        len      (count no-dash)
        valid    (and (re-matches #"^(?:(\d+)\-?)+[X\d]$" isbn)
                      (= 10 len))]
    (if valid
      (= 0 (mod
            (->> no-dash
                 reverse
                 (into [])
                 (reduce-kv isbn-checksum 0)) 
            11))
      false)))