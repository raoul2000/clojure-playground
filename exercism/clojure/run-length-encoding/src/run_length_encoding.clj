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

(defn create-char-segment
  "create a string of len character c, where len is a string representing
   the length of the result string"
  [c len]
  (apply str (repeat (if (= len "0") 1 (Integer/parseInt len)) c)))

(defn run-length-decode
  "decodes a run-length-encoded string"
  [cipher-text]
  (loop [c-txt cipher-text
         cnt "0"
         result ""]
    (if (empty? c-txt)
      result
      (let [cur-char           (first c-txt)
            cur-char-is-digit? (Character/isDigit cur-char)]
        (recur (rest c-txt)
               (if cur-char-is-digit? (str cnt cur-char) "0")
               (if cur-char-is-digit?
                 result
                 (format "%s%s" result (create-char-segment cur-char cnt))))))))
