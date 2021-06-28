(ns say)

;; eleven twelve thrirteen fourteen fifteen sixteen seventeen eighteen nineteen

(def root ["twen" "thir" "four" "fif" "six" "seven" "eigh" "nine"])
(defn say-teen [n]
  (when (< 12 n 20)
    (str (get root (- n 12)) "teen")))

(comment
  (say-teen 19))

(defn say-ty [n]
  (when (< 19 n 100)
    (str (get root (- (quot n 10) 2)) "ty")))

(comment
  (say-ty 30)
  (say-ty 20)
  (say-ty 90))


(defn say<10
  "say n when lower than 10 (exclusive)"
  [n]
  (get ["one" "two" "three" "four" "five" "six" "seven" "eight" "nine"] (dec n)))

(comment
  (say<10 8))

(defn say-x10
  "say n when multiple of 10"
  [n]
  (condp = n
    10 "ten"
    20 "twenty"
    (say-ty n)))

(comment
  (say-x10 10)
  (say-x10 20)
  (say-x10 50)
  (say-x10 90))

;; one two three ... nine
;; ten elven twelve
;; thirteen ... nineteen
;; twenty twenty-one twenty-two
;; thirty thirty-one ... ninety-nine

(defn say-11-to-19
  "say n when between 11 and 19 (both inclusive) "
  [n]
  (condp = n
    11 "eleven"
    12 "twelve"
    (say-teen n)))

(comment
  (say-11-to-19 19)
  (say-11-to-19 10)
  (say-11-to-19 11))

(defn  say<99-1 [n]
  (cond
    (< n 10)           (say<10       n)
    (zero? (rem n 10)) (say-x10      n)
    (< n 20)           (say-11-to-19 n)
    :else (str
           (say-ty n)
           "-"
           (say<10 (rem n 10)))))

(defn  say<99 [n]
  (cond
    (= n 10)           "ten"
    (= n 11)           "eleven"
    (= n 12)           "twelve"
    (< n 10)           (say<10    n)
    (< n 20)           (say-teen  n)
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