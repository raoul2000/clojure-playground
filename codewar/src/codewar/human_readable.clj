(ns codewar.human-readable
  (:require [clojure.string :refer [join]]))

;; https://www.codewars.com/kata/52685f7382004e774f0001f7/train/clojure

(defn human-readable-1
  [x]
  (->> [3600 60 1]
       (reduce (fn [acc divisor]
                 (let [remainder (:rem acc)
                       tokens    (:tok acc)]
                   (assoc
                    acc
                    :tok (conj tokens (format "%02d" (quot remainder divisor)))
                    :rem (rem remainder divisor)))) {:tok [] :rem x})
       (:tok)
       (join ":")))

(defn human-readable
  [x]
  (loop [[divisor & other] [3600 60 1]
         remainder         x
         result            []]
    (if-not divisor
      (join ":" result)
      (recur other
             (rem remainder divisor)
             (conj result (format "%02d" (quot remainder divisor)))))))

(comment

  (reduce (fn [acc p]
            (assoc
             acc
             :s (conj (:s acc) (quot (:rem acc) p))
             :rem  (rem (:rem acc) p))) {:s [] :rem 86399} [3600 60 1])

  ;; second into hours
  (quot 59 60)
  (quot 90 60)
  (quot 86399 3600)
  (quot 359999 3600)
  (quot (rem 359999 3600) 60)

  (join ":" [22 32 66])
  (join ":" [2 3 6])
  (str (apply #(format "%02d" %) [ 1 2]))
  (map #(format "%02d" %) [ 1 2])



  (= (human-readable      0) "00:00:00")
  (= (human-readable     59) "00:00:59")
  (= (human-readable     60) "00:01:00")
  (= (human-readable     90) "00:01:30")
  (= (human-readable  86399) "23:59:59")
  (= (human-readable 359999) "99:59:59")

  ;;
  )