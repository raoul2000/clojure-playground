(ns run-length-encoding-wip)

(defn run-length-encode-1
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

(defn run-length-encode
  "encodes a string with run-length-encoding"
  [plain-text]
  (reduce #(str %1 (if (= 1 (count %2))
                     (str (first %2))
                     (str (count %2) (first %2))))
          ""
          (partition-by identity plain-text)))

(comment
  (run-length-encode "zzz ZZ  zZ")
  (run-length-encode "a  a"))

(defn red
  [s]
  (reduce #(cond
             (= (first (last %1)) %2) (update-in %1 [(dec (count %1)) 1] inc)
             :else (conj %1 [%2 1]))
          []
          (vec s)))

(defn strmap->str
  [m]
  (reduce #(str %1
                (first %2)
                (when (> (last %2) 1)
                  (last %2))) "" m))

(comment

  (reduce #(str %1
                (first %2)
                (if (> (last %2) 1)
                  (last %2))) "" [[\a 1] [\b 3] [\c 1]])
  (strmap->str [[\space 1] [\a 2] [\space 1] [\z 1]])


  (red "aaaabbaabc")
  (red " a z")

  (def e1 (frequencies "bcaaaaBB"))
  e1
  (reduce-kv #(str %1 %3 %2) "" e1)
  (reduce-kv #(str %1 (when (> %3 1) %3) %2) "" e1)
  (reduce-kv #(str %1 (when (> %3 1) %3) %2) "" {\a 1})

  (let [is-item? #(= % \a)]
    (take-while is-item? [\c \a \a \b]))

  (reduce #(cond
             (= (first (last %1)) %2) (update %1 [(dec (count %1)) 1] inc)
             :else (conj %1 [%2 1]))
          []
          (vec "aabc")))



(defn run-length-decode-1
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
         (if (Character/isLetter cur-char)
           (let [str-segment (apply str (repeat (if (= cnt "0") 1 (Integer/parseInt cnt)) cur-char))]
             (format "%s%s" result str-segment))
           result))))))

(defn run-length-decode-2
  "decodes a run-length-encoded string"
  [cipher-text]
  (reduce #(if (= 1 (count %2))
             (str %1 %2)
             (let [match (first (re-seq #"(\d+)\D" %2))
                   cnt (Integer/parseInt (last match))
                   letter (last %2)]
               (str %1 (apply str (repeat cnt letter)))))
          ""
          (re-seq #"\d+\D|\D" cipher-text)))

(defn run-length-decode
  "decodes a run-length-encoded string"
  [cipher-text]
  (reduce #(str %1 (if (= 1 (count %2))
                     %2
                     (let [match (first (re-seq #"(\d+)\D" %2))
                           count (Integer/parseInt (last match))
                           lettr (last %2)]
                       (apply str (repeat count lettr)))))
          ""
          (re-seq #"\d+\D|\D" cipher-text)))

(comment
  (run-length-decode "x2a2b10c")
  ;;"2A3B4CX" => "AABBBCCCCX"
  (reduce #(cond
             (empty? %1) (conj %1 [nil  %2])
             (Character/isDigit %2) (update-in %1 [(dec (count %1)) 1] identity)) [] "22A3B4CX")

  (loop [s "aabcaaaa  r"
         count 1
         result ""]
    (if (empty? s)
      result
      (let [cur-char (first s)
            next-char (second s)]
        (recur (rest s)
               (if (= next-char cur-char) (inc count) 1)
               (if (= next-char cur-char)
                 result
                 (str result count cur-char))))))

  ;; encode ------------------------------
  (loop [s "aabcaaaa  rrr"
         count 1
         result ""]
    (if (empty? s)
      result
      (let [cur-char (first s)
            same-next? (= cur-char (second s))]
        (recur (rest s)
               (if same-next? (inc count) 1)
               (if same-next?
                 result
                 (str result (if (= 1 count) nil count) cur-char))))))

  (loop [s "r2a13bcd"
         count "0"
         result ""]
    (if (empty? s)
      result
      (let [cur-char (first s)]
        (recur
         (rest s)
         (if (Character/isDigit cur-char)
           (str count cur-char)
           "0")
         (if (Character/isLetter cur-char)
           (str result (apply str (repeat (Integer/parseInt count) cur-char)))
           result))))))
