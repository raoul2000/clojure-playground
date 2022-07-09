(ns largest-series-product)

(defn create-digit-seq [s]
  (map #(Character/digit % 10) s))


(defn largest-product [n str-digits]
  (cond
    (zero? n)
    1

    (or (neg? n)
        (not (re-matches #"[0-9]+" str-digits))
        (> n (count str-digits)))
    (throw (Exception. "invalid input"))

    :else
    (->> (create-digit-seq str-digits)
         (partition n 1)
         (map #(apply * %))
         (apply max))))
