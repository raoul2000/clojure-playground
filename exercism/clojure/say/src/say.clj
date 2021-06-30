(ns say
  (:require [clojure.string :refer [join trimr]]))

(def digit ["zero" "one" "two" "three" "four" "five" "six" "seven" "eight" "nine"])
(def root  ["twen" "thir" "for" "fif" "six" "seven" "eigh" "nine"])

(defn say-digit [n] (get digit  n))

(defn say-teen
  "say n when 12 < n < 20, nil otherwise
   ex: thirteen, fourteen, ... nineteen"
  [n]
  (when (< 12 n 20)
    (str (get root (- n 12)) "teen")))

(defn say-ty
  "say n when divisible by 10 and  10 < n < 100, nil otherwise
  ex :  twenty, thirty, ... ninety"
  [n]
  (when (< 19 n 100)
    (str (get root (- (quot n 10) 2)) "ty")))

(defn  say<99 [n]
  (cond
    (< n 10)  (say-digit n)
    (= n 10)  "ten"
    (= n 11)  "eleven"
    (= n 12)  "twelve"
    (= n 14)  "fourteen"
    (< n 20)  (say-teen  n)
    :else (str
           (say-ty n)
           (let [r (rem n 10)]
             (when (pos? r) (str "-" (say-digit r)))))))

(defn say<999 [n]
  (if (< n 99)
    (say<99 n)
    (str
     (say-digit (quot n 100))
     " hundred"
     (let [r (rem n 100)]
       (when (pos? r) (str " " (say<99 r)))))))

(defn split-by-3 [n]
  (->> n
       str
       reverse
       (partition-all 3)
       reverse
       (map #(Integer/parseInt (apply str (reverse %))))))

(defn add-words [xs]
  (->> ["trillion" "billion" "million" "thousand" ""]
       (take-last (count xs))
       (map vector xs)
       (filter (comp pos? first))
       flatten))

(defn number [num]
  (cond
    (not (< -1 num 999999999999))    (throw (IllegalArgumentException.))
    (< num 10)                      (say-digit num)
    (< num 999)                     (say<999 num)
    :else (->> num
               split-by-3
               add-words
               (map #(if (number? %) (say<999 %) %))
               (join " ")
               trimr)))