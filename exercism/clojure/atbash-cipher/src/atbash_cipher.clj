(ns atbash-cipher)


(comment
  (filter #(Character/isLetterOrDigit %) (seq "123a,/3\\F"))
  (map #(Character/toLowerCase %) "ac")
  (int \a) ;; 97
  (int \z) ;; 122
  )
;; 97 => 122 : 122 + 97 - 97
;; 98 => 121 : 122 + 97 - 98
;; 99 => 120 : 122 + 97 - 99

;; 122 => 97 : 122 + 97 - 122
(comment
  (defn tr [c]
    (char (- 219 (int c)))) ;; 219 = (+ (int \a) (int \z))
  (tr \z)
  (tr \b)
  (tr \a))


(defn translate-char
  "apply atbash conversion to alphabetic char *c*. Digit chars are not modified"
  [c]
  (if (Character/isDigit c)
    c
    (char (- 219 (int c)))))                   ;; 219 = (+ (int \a) (int \z))

(defn char-seq->string
  "Given a char seq, returns a string of chars words with a max len of 5"
  [xs]
  (->> xs
       (partition 5 5 [])
       (map #(apply str %))
       (interpose " ")
       (apply str)))

(defn encode [s]
  (->> s
       (filter #(Character/isLetterOrDigit %)) ;; remove non alphu-num chars
       (map    #(Character/toLowerCase %))     ;; to lowercase
       (map    translate-char)                 ;; apply atbash-cipher
       char-seq->string))

(comment
  (encode "omg")
  (encode ""))