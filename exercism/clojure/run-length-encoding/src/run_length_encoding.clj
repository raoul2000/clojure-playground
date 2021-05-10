(ns run-length-encoding)

(defn run-length-encode
  "encodes a string with run-length-encoding"
  [plain-text]
  (loop [p-txt plain-text
         cnt 1
         result ""]
    (if (empty? p-txt)
      result
      (let [cur-char (first p-txt)
            same-next? (= cur-char (second p-txt))]
        (recur (rest p-txt)
               (if same-next? (inc cnt) 1)
               (if same-next?
                 result
                 (str result (if (= 1 cnt) nil cnt) cur-char)))))))

(defn run-length-decode
  "decodes a run-length-encoded string"
  [cipher-text]
  (loop [c-txt cipher-text
         cnt "0"
         result ""]
    (if (empty? c-txt)
      result
      (let [cur-char (first c-txt)]
        (recur
         (rest c-txt)
         (if (Character/isDigit cur-char)
           (str cnt cur-char)
           "0")
         (if (Character/isDigit cur-char)
           result
           (let [str-segment (apply str (repeat (if (= cnt "0") 1 (Integer/parseInt cnt)) cur-char))]
             (format "%s%s" result str-segment))
           ))))))

(comment
  (run-length-encode "zzz ZZ  zZ")
  (run-length-decode "3z 2Z2 zZ")
  (run-length-decode "2 a")
  (run-length-encode "a  a"))
