(ns series
  (:require [clojure.string :as str]))

(defn slices [string length]
  (cond
    (or  (= 0 (count string)) (> length (count string))) []
    (>= 0 length) [""]
    :else (into
           (vector (subs string 0 length))
           (slices
            (subs string 1)
            length))))


(comment
  (slices "123456" -3)
  (slices "" 1)
  (slices "123" 0)
  (slices "123" 1000)
  (slices "123" 3)
  (slices "123456789abcdef" 2)
  (into [1 2] [3 4])
  (into [] [2 3])
  (vector 1 2 3)
  (def txt "123456")
  (subs txt 0 2)
  (list (subs txt 0 2) (subs txt 2))
  (str/split "abc" #"")
  (subs "abcd" 0 2)
  (subs "abcd"  2)
  (count "123")
  (concat [1 2] [3 4])
  (concat '(1 2) '(3 4))
  (or true false)
  ()
  (set []))
