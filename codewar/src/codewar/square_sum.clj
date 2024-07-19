(ns codewar.square-sum)


;; https://www.codewars.com/kata/515e271a311df0350d00000f/train/clojure

(defn square-sum [lst]
  (->> lst
       (map #(* % %))
       (apply +)))
  