(ns say
  (:require [clojure.string :refer [join trimr]]))

(def spec {0 "zero"     1  "one"       2  "two"      3  "three"
           4 "four"     5  "five"      6  "six"      7  "seven"
           8 "eight"    9  "nine"      10 "ten"      11 "eleven"
           12 "twelve"  13 "thirteen"  14 "fourteen" 15 "fifteen"
           16 "sixteen" 17 "seventeen" 18 "eighteen" 19 "ninteen"
           20 "twenty"  30 "thirty"    40 "forty"    50 "fifty"
           60 "sixty"   70 "seventy"   80 "eighty"   90 "ninety"})

(defn say<20
  "say n when n < 20, nil otherwise
  ex: one, two, ...ten, eleven, twelve, thirteen, ... nineteen"
  [n]
  (when (< n 20) (spec n)))

(defn say-the-ten
  "say the ten of n when 19 < n < 100, nil otherwise
  ex :  twenty, thirty, ... ninety"
  [n]
  (when (< 19 n 100)
    (spec (* 10 (quot n 10)))))

(defn  say<100
  "say n when n < 1000, nil otherwise
  ex: one, twenty-one, fifty, ... ninety-nine"
  [n]
  (cond
    (< n 20)   (say<20  n)
    (< n 100)  (str
                (say-the-ten n)
                (let [r (rem n 10)]
                  (when (pos? r) (str "-" (say<20 r)))))))

(defn say<1000
  "say n when n < 1000, nil otherwise
  ex: three hundred tenwty-five ...nine hundred ninty-nine"
  [n]
  (cond
    (< n 99)   (say<100 n)
    (< n 1000) (str
                (say<20 (quot n 100))
                " hundred"
                (let [r (rem n 100)]
                  (when (pos? r) (str " " (say<100 r)))))))

(defn split-by-3-digits
  "split num into max of 3 digits groups
  ex: 1 223 656 => (656 223 1)"
  [num]
  (->> (iterate    (fn [[n _]] [(quot n 1000) (rem n 1000)]) [num 0])
       (take-while (fn [[n r]] (not= n r 0)))
       rest
       (map last)))

(defn add-scale-words [xs]
  (->>  (map vector xs ["" "thousand" "million"  "billion" "trillion"])
        (filter (comp pos? first))
        reverse
        flatten))

(defn number [num]
  (cond
    (not (< -1 num 999999999999))   (throw (IllegalArgumentException.))
    (< num 999)                     (say<1000 num)
    :else (->> num
               split-by-3-digits
               add-scale-words
               (map #(if (number? %) (say<1000 %) %))
               (join " ")
               trimr)))
