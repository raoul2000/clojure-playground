(ns say-wip
  (:require [clojure.string :refer [join trimr]]))

(def digit  ["one" "two" "three" "four" "five" "six" "seven" "eight" "nine"])
(def root ["twen" "thir" "for" "fif" "six" "seven" "eigh" "nine"])

(defn say<10
  "say n when n < 10, nil otherwise
   ex: one, two ... nine"
  [n]
  (when (< n 10)
    (get digit (dec n))))

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

;; one two three ... nine
;; ten elven twelve
;; thirteen ... nineteen
;; twenty twenty-one twenty-two
;; thirty thirty-one ... ninety-nine

(defn  say<99 [n]
  (cond
    (< n 10)  (say<10    n)
    (= n 10)  "ten"
    (= n 11)  "eleven"
    (= n 12)  "twelve"
    (= n 14)  "fourteen"
    (< n 20)  (say-teen  n)
    :else (str
           (say-ty n)
           (let [r (rem n 10)]
             (when (pos? r) (str "-" (say<10 r)))))))

(defn say<999 [n]
  (if (< n 99)
    (say<99 n)
    (str
     (say<10 (quot n 100))
     " hundred"
     (let [r (rem n 100)]
       (when (pos? r) (str " " (say<99 r)))))))

(comment
  (say<999 100)
  (say<999 523)
  (say<999 978)
  ;;
  )

(defn split-by-3 [n]
  (->> n
       str
       reverse
       (partition-all 3)
       reverse
       (map #(Integer/parseInt (apply str (reverse %))))))

(defn split-by-3-digits-alter
  "split n into max of 3 digits groups
   ex: 1 223 656 => (1 223 656)"
  [n]
  (loop [xs (str n)
         r []]
    (if (empty? xs)
      r
      (recur
       (drop-last 3 xs)
       (cons (Integer/parseInt (join (take-last 3 xs))) r)))))

(comment
  (split-by-3 5)
  (split-by-3 55)
  (split-by-3 1236))

(defn split-by-3-1 [n]
  (loop [s (map #(Character/digit % 10) (str n))
         r []]
    (if (empty? s)
      r
      (recur
       (drop-last 3 s)
       (cons (apply str (take-last 3 s)) r)))))

(comment
  (split-by-3-1 0)
  (split-by-3-1 1234)
  ;;
  )

(defn add-words [xs]
  (->> ["trillion" "billion" "million" "thousand" ""]
       (take-last (count xs))
       (map vector xs)
       (filter (comp pos? first))
       flatten))

(comment
  (add-words [1 0 3])
  (add-words [1 5 3])
  ;;
  )


(defn number [num] ;; <- arglist goes here
  (cond
    (not (< -1 num 999999999999))    (throw (IllegalArgumentException.))
    (= 0 num)                       "zero"
    (< num 999)                     (say<999 num)
    :else (->> num
               split-by-3
               add-words
               (map #(if (number? %) (say<999 %) %))
               (join " ")
               trimr)))

(comment
  (number 0)
  (number 1000000000)
  (number 100)
  (number 1236)
  (number 112236)
  (number 98112236))

(comment

  ((juxt #(quot % 10) #(rem % 10)) 56)
  ((juxt #(quot % 10) #(rem % 10)) 50)

  (let [[u d] [(rem 5 10)    (quot 5 10)]]
    [u d])

  (number -1)
  (say<10 9)


  (map say<99 [1 2 3 4])
  (map say<99 [10 11 12 13 14 19])
  (map say<99 [10 20 30 40 50 90])
  (map say<99 [11 21 31 41 51 91])
  (say<99 50)
  (say<99 51)
  (say<99 11)
  (say<99 12)
  (say<99 15)
  (say<99 20)
  (say<99 21)
  (say<99 22)
  (say<99 31)
  (say<99 40)
  (say<99 54)
  (say<99 99)
  ;;
  )

(comment
  (def trillion 1000000000000)
  ;; 1
  ;; 1 000 ..................... thousand
  ;; 1 000 000 .................. million
  ;; 1 000 000 000 000 .......... billion
  ;; 1 000 000 000 000 000 000 .. trillion
  (rem 1234567890 1000) ;; => 890
  (rem (quot (- 1234567890 890) 1000) 1000)               ;; => 567
  (rem (quot (- 1234567890 890 567) 1000000) 1000)        ;; => 234
  (rem (quot (- 1234567890 890 567 234) 1000000000) 1000) ;; => 1

  (rem 1000 1000)                   ;; => 0
  (rem (quot (- 1000 0) 1000) 1000) ;; => 1


  (rem 2534 1000)                       ;; => 534
  (rem (quot (- 2534 534) 1000) 1000)   ;; => 2

  ;; considering string
  (partition-all 3 "1234567")
  (->> "23456789"
       reverse
       (partition-all 3)
       reverse
       (map #(Integer/parseInt (apply str (reverse %)))))  ;; => (1 432 765)

  (def trillion 1000000000000000000)
  (take 7 (iterate (fn [[a b]] (vector (quot a 1000) (rem a 1000))) [23456789889 0]))
  (take 7 (iterate (fn [n](rem n 1000)) 23456789889 ))
  
  (reverse (map last (rest (take-while (fn [[a b]] (not= a b 0)) 
              (iterate 
               (fn [[a _]] (vector (quot a 1000) (rem a 1000))) 
               [23456789889 0])))))


  (take 7 (iterate (fn [[a b]] (vector (quot a 1000) (rem a 1000))) [1000000 0]))
   (take 10 (iterate #(rem % 1000) 23456789889))

  (defn div-thousand [n]
    (last (take-while (comp pos? first)  (iterate (fn [[a b]] (vector (quot a 1000) (* b 1000))) [n 1]))))


  (loop [n 1234567
         res []]
    (if-not (pos? n)
      res
      (let [[tok th] (div-thousand n)]
        (recur
         (- n (* tok th))
         (conj res tok)))))

  ["tri" "bi" "mi" "thou" ""]
  [1 23 3]
  [ 3 23 1]
  ;;
  )