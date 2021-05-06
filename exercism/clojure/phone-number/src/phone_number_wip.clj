(ns phone-number-wip)

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

(comment
  (= "9876543210" (phone-number/number "19876543210"))
  (Character/isLetter \.)
  (Character/isDigit \1)
  (first "abc")
  (nth "abc" 2)
  (apply str (filter #(Character/isDigit %) "ab3c")))

(defn area-code [num-string] ;; <- arglist goes here
  (apply str (take 3 (number num-string))))

(comment
  (phone-number/area-code "12234567890"))

(defn pretty-print [num-string] ;; <- arglist goes here
  (let [num (number num-string)
        area-code (apply str (take 3 num))
        ex-code (subs num 3 6)
        subs-num (subs num 6)]
    (str
     "(" area-code ") " ex-code "-" subs-num )))

(comment
  (pretty-print "2234567890")
  )
