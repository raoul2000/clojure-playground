(ns armstrong-numbers)

(defn tokenize-int
  [n]
  (->> n
       (iterate #(quot % 10))
       (take-while pos?)
       (map #(mod % 10))))

(defn pow-n
  [n num]
  (apply * (repeat n num)))

(defn armstrong? [num]
  (let [num-seq (tokenize-int num)
        pow     (partial pow-n (count num-seq))]
    (->> num-seq
         (map pow)
         (apply +)
         (= num))))