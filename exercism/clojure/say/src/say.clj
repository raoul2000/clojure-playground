(ns say)

(def digit  ["one" "two" "three" "four" "five" "six" "seven" "eight" "nine"])
(def root ["twen" "thir" "four" "fif" "six" "seven" "eigh" "nine"])

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
    (< n 20)  (say-teen  n)
    :else (str
           (say-ty n)
           (let [r (rem n 10)]
             (when (pos? r) (str "-" (say<10 r)))))))

(defn number [num] ;; <- arglist goes here
  (if-not (< 0 num 999999999999)
    (throw (IllegalArgumentException.))))


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
  (rem (quot (- 2534 534) 1000) 1000))  ;; => 2