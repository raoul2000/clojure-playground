(ns codewar.passphrases)

;; https://www.codewars.com/kata/559536379512a64472000053/train/clojure

(defn shift-letter [n c]
  (if (Character/isLetter c)
    (let [base (if (Character/isUpperCase c)
                 (int \A)
                 (int \a))]
      (char (+ base (mod (+ (- (int c) base) n) 26))))
    c))

(defn complement-9 [c]
  (if (Character/isDigit c)
    (char (+ (int \0) (- (int \9) (int c))))
    c))

(defn letter-case [i c]
  (if (even? i)
    (Character/toUpperCase c)
    (Character/toLowerCase c)))

(defn play-pass [s n]
  (let [shift-letter-n (partial shift-letter n)]
    (->> s
         (map          shift-letter-n)
         (map          complement-9)
         (map-indexed  letter-case)
         (reverse)
         (apply str))))
