(ns largest-series-product)

(defn create-digit-seq [s]
  (->> (map #(Character/digit % 10) s)
       (drop-while zero?)))

(comment
  (create-digit-seq "123")
  (create-digit-seq "00123")
  ;;
  )

(defn largest-product [n str-digits]
  (when (or (neg? n)
            (not (re-matches #"[0-9]+" str-digits))
            (> n (count str-digits)))
    (throw (Exception. "invalid input")))
  (cond
    (zero? n) 1
    :else     (->> str-digits
                   (create-digit-seq)
                   (partition n 1)
                   (map #(apply * %))
                   (apply max))))

(comment

  (->> (create-digit-seq "12304567089")
       (partition 5 1)
       (map #(apply * %))
       (apply max))

;;
  )
