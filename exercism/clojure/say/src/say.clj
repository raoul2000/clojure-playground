(ns say
  (:require [clojure.string :refer [join trimr]]))

(def digit ["zero" "one" "two" "three" "four" "five" "six" "seven" "eight" "nine"])
(def root  ["twen" "thir" "for" "fif" "six" "seven" "eigh" "nine"])

(defn say-digit
  "say n when -1 < n < 10, nil otherwise
   ex: zero, one, .... nine"
  [n] (get digit  n))

(defn say-teen
  "say n when 9 < n < 20, nil otherwise
   ex: ten, eleven, twelve, thirteen, ... nineteen"
  [n]
  (case n
    10 "ten"
    11 "eleven"
    12 "twelve"
    14 "fourteen"
    (when-let [s (get root (- n 12))]
      (str s "teen"))))

(defn say-the-ten
  "say the ten of n when 19 < n < 100, nil otherwise
  ex :  twenty, thirty, ... ninety"
  [n]
  (when-let [s (get root (- (quot n 10) 2))]
    (str s "ty")))

(defn  say<99
  "say n when n < 99, nil otherwise
   ex: one, twenty-one, fifty, ... ninety-nine"
  [n]
  (condp > n
    10   (say-digit n)
    20   (say-teen  n)
    100  (str
          (say-the-ten n)
          (let [r (rem n 10)]
            (when (pos? r) (str "-" (say-digit r)))))
    nil))

(defn say<999
  "say n when n < 999, nil otherwise
   ex: three hundred tenwty-five ...nine hundred ninty-nine"
  [n]
  (condp > n
    99   (say<99 n)
    1000 (str
          (say-digit (quot n 100))
          " hundred"
          (let [r (rem n 100)]
            (when (pos? r) (str " " (say<99 r)))))
    nil))

(defn split-by-3-digits
  "split n into max of 3 digits groups
   ex: 1 223 656 => (1 223 656)"
  [n]
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
    (not (< -1 num 999999999999))   (throw (IllegalArgumentException.))
    (< num 999)                     (say<999 num)
    :else (->> num
               split-by-3-digits
               add-words
               (map #(if (number? %) (say<999 %) %))
               (join " ")
               trimr)))