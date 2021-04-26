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


(defn armstrong-orig? [num]
  (let [num-seq (tokenize-int num)
        pow     (partial pow-n (count num-seq))]
    (= num (apply + (map pow num-seq)))))


(comment
  (reverse (mapv #(mod % 10) (take-while pos? (iterate #(quot % 10) 3251))))


  (reduce + (map #(* %1 %1 %1) (str 153)))
  (reduce + (map #(reduce * 1 (repeat 3 (Integer. %))) (str 153)))

  (map #(Character/digit %1 10) (str 153))
  (apply * (repeat 3 (Character/digit \2 10)))

  (tokenize-int 123887)

  (pow-n 3 3)

  (armstrong? 0)
  (armstrong? 5)
  (armstrong? 153)
  (armstrong? 9474)
  (armstrong? 947)
  (armstrong? 9926315))
