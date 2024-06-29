(ns codewar.better-then-average)

;; https://www.codewars.com/kata/5601409514fc93442500010b/train/clojure

(defn better_than_average [class_points your_points]
  (let [avg (/ (apply + class_points) (count class_points))]
    (> your_points avg)))