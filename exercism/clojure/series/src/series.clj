(ns series)

(defn slices [string length]
  (cond
    (or  (= 0 (count string)) (> length (count string))) []
    (>= 0 length) [""]
    :else (into
           (vector (subs string 0 length))
           (slices
            (subs string 1)
            length))))
