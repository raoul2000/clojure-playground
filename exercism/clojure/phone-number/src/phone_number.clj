(ns phone-number)

(defn number [num-string] ;; <- arglist goes here
  (let [num (apply str (filter #(Character/isDigit %) num-string))
        len (count num)
        first-digit (first num)
        first-ex-digit (nth num 3)
        error "0000000000"]
    (cond
      (and (= 11 len) (= \1 first-digit)) (apply str (rest num))
      (= \0 first-digit) error
      (= \1 first-digit) error
      (= \0 first-ex-digit) error
      (= \1 first-ex-digit) error
      (= 11 len) error
      (= 9 len) error
      :else num)))

(defn area-code [num-string]
  (subs (number num-string) 0 3))

(defn pretty-print [num-string] ;; <- arglist goes here
  (let [num (number num-string)
        area-c (area-code num-string)
        ex-code (subs num 3 6)
        subs-num (subs num 6)]
    (str
     "(" area-c ") " ex-code "-" subs-num)))

