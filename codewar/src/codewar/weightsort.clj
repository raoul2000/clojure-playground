(ns codewar.weightsort
  (:require [clojure.string :as s]))

;; https://www.codewars.com/kata/55c6126177c9441a570000cc/train/clojure

(defn sum-digits [s]
  (reduce
   (fn [acc c] (+ acc (Character/digit c 10))) 0 s))

(comment
  (sum-digits "100")
  (sum-digits "123")
  (map vector ["1" "23"] [1 23]))


(defn add-weight [s-list]
  (let [n-lst (map sum-digits s-list)]
    (map vector s-list n-lst)))

(comment
  (add-weight ["1" "123"]))


(defn split-num [s]
  (re-seq #"\d+" s))

(comment
  (split-num " 1 665 23   6 554"))

(defn order-weight [s]
  (->> s
       split-num
       add-weight
       (sort-by (juxt second first))
       (map first)
       (s/join " ")))

(comment
  (order-weight "56 65 74 100 99 68 86 180 90")
  ;; => "100 180 90 56 65 74 68 86 99"
  (re-seq #"\d+" "  1 23 65")
  (s/join " " (map first '(["100" 1] ["180" 9] ["90" 9] ["56" 11] ["65" 11] ["74" 11] ["68" 14] ["86" 14] ["99" 18])))

  ;;
  )