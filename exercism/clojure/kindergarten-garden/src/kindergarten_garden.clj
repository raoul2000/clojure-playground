(ns kindergarten-garden
  (:require [clojure.string :refer [split]]))

(defn garden [] ;; <- arglist goes here
  ;; your code goes here
)

(comment
  (def s "VVCCGG\nVVCCGG")
  (split s #"\n")
  (def l1 (map (partial into []) (split s #"\n")))

  (map (fn [a b ] (vector a b )) [:a :b :c] [1 2 3])
  (map (fn [a b ] (vector a b )) [:a :b :c] [1 2 3])

  (take 2 [:a :b :c :d])
  (apply (comp take 2) [:a :b :c :d] [1 2 3 4])
  (juxt (comp take 2) [:a :b :c :d] [1 2 3 4])
  


  )