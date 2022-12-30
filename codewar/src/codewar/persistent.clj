(ns codewar.persistent)

(defn persistence [num]
  (loop [num num
         i   0]
    (if (< num 10)
      i
      (recur (apply *  (map #(Character/digit % 10) (str num)))
             (inc i)))))

(comment
  (persistence 39)
  (persistence 4)
  (persistence 25)
  (persistence 999)
  ;;
  )