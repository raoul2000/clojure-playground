(ns series
  (:require [clojure.string :as str]))

(defn slices [string length] ;; <- arglist goes here
  ;; your code goes here
  (if (> length (count string))
    (if (= 0 (count string))
      []
      [string])
    (into
     (vector (subs string 0 length))
     (slices
      (subs string length)
      length))))


(comment
  (slices "123456" 3)
  (slices "" 1)
  (slices "123" 0)
  (slices "123" 1000)
  (slices "123" 3)
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
  ()
  (set []))