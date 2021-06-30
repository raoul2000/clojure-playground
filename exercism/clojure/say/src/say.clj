(ns say
  (:require [clojure.string :refer [join trimr]]))

(def spec {0 "zero", 1 "one",   2 "two",   3 "three", 4 "four", 5 "five"
           6 "six",  7 "seven", 8 "eight", 9 "nine",  10 "ten"
           11 "eleven", 12 "twelve", 14 "fourteen"})
(def root  ["twen" "thir" "for" "fif" "six" "seven" "eigh" "nine"])

(defn say<20
  "say n when n < 20, nil otherwise
   ex: one, two, ...ten, eleven, twelve, thirteen, ... nineteen"
  [n]
  (or
   (get spec n)
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
    20   (say<20  n)
    100  (str
          (say-the-ten n)
          (let [r (rem n 10)]
            (when (pos? r) (str "-" (say<20 r)))))
    nil))

(defn say<999
  "say n when n < 999, nil otherwise
   ex: three hundred tenwty-five ...nine hundred ninty-nine"
  [n]
  (condp > n
    99   (say<99 n)
    1000 (str
          (say<20 (quot n 100))
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
       (map #(Integer/parseInt (join (reverse %))))))

(defn add-scale-words [xs]
  (->>  ["trillion" "billion" "million" "thousand" ""]
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
               add-scale-words
               (map #(if (number? %) (say<999 %) %))
               (join " ")
               trimr)))
