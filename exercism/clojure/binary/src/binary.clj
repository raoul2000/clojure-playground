(ns binary)

(defn to-decimal [s]
  (if (re-matches #"[01]+" s)
    (->> s
         reverse
         (map #(Character/digit %1 10))
         (into [])
         (reduce-kv #(+ %1 (* %3 (Math/pow 2 %2))) 0)
         int)
    0))

(defn to-decimal-alt [s]
  (if (re-matches #"[01]+" s)
    (let [s-len (dec (count s))]
      (->> (into [] s)
           (reduce-kv #(if (= \1 %3)
                         (+ %1 (Math/pow 2 (- s-len %2)))
                         %1) 0)
           int))
    0))


(reduce-kv (fn [res k v]
             (if (= \1 v)
               (+ res (Math/pow 2 (- 2 k)))
               res))
           0  [\1 \1 \1])

(to-decimal "1")
