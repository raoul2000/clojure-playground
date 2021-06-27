(ns say)

;; eleven twelve thrirteen fourteen fifteen sixteen seventeen eighteen nineteen
(def spec {10 "ten"
           11 "eleven"
           12 "twelve"
           20 "twenty"})

(def root ["thir" "four" "fif" "six" "seven" "eigh" "nine"])

(defn say<10 [n]
  (get ["one" "two" "three" "four" "five" "six" "seven" "eight" "nine"] (dec n)))

(defn say<20 [n]
  (condp = n
    10 "ten"
    11 "eleven"
    12 "twelve"
    (str (get root (- n 13)) "ty")))

(defn  say<99 [n]
  (if-let [s (get spec n)]
    s
    (cond
      (< n 10) (say<10 n)
      (< n 20) (str (get root (- n 13)) "teen")
      (< n 30) (str "twenty-" (say<10 (- n 20)))
      :else (str
             (get root (- (quot n 10) 3))
             "ty-"
             (say<10 (rem n 10))))))

(defn number [num] ;; <- arglist goes here
  (if-not (< 0 num 999999999999)
    (throw (IllegalArgumentException.))))




(comment
  (number -1)
  (say<10 9)

  (say<20 12)
  (say<20 19)

  (say<99 11)
  (say<99 15)
  (say<99 20)
  (say<99 21)
  (say<99 40)
  (say<99 54)
  ;;
  )