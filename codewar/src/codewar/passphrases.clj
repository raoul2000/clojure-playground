(ns codewar.passphrases)

;; https://www.codewars.com/kata/559536379512a64472000053/train/clojure

(defn tr-char 
  [c n]
  (cond
    (Character/isLetter c) (let [base (if (Character/isUpperCase c)
                                        (int \A)
                                        (int \a))]
                             (char (+ base (mod (+ (- (int c) base) n) 26))))
    (Character/isDigit c)  (char (+ (int \0) (- (int \9) (int c))))
    :else c))

(defn tr-case [c i]
  (if (even? i)
    (Character/toUpperCase c)
    (Character/toLowerCase c)))

(defn play-pass [s n]
  (->> s
       (map         #(tr-char %1 n))
       (map-indexed #(tr-case %2 %1))
       (reverse)
       (apply str)))


(comment
  (char (+ (int \Y) (mod 2 26)))

  (char (+ (int \A) (mod (+ (- (int \Z) (int \A)) 1) 26)))


  (tr-char \Y 2)
  (= (play-pass "I LOVE YOU!!!" 1) "!!!vPz fWpM J")
  (= (play-pass "MY GRANMA CAME FROM NY ON THE 23RD OF APRIL 2015" 2)
     "4897 NkTrC Hq fT67 GjV Pq aP OqTh gOcE CoPcTi aO")
  ;; \A = 65
  ;; \Z = 90
  ;; \a = 97
  ;; \z = 122

  (int \Z)
  (Character/isLetter)
  (Character/isUpperCase \A)

  (char (+ (int \0) (- (int \9) (int \0))))

  (+ (int \a) (mod 25 26))
  (char (+ (int \A) (mod 0 26))))