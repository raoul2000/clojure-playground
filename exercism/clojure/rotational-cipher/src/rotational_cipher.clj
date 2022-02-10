(ns rotational-cipher)



(comment
  (map identity "a")
  (map #(Character/getNumericValue %) "azAZ")
  ;; 10 35 10 35
  ;; not the good method

  (map #(int %) "azAZ")
  ;; (97 122 65 90)
  ;; yes

  (->> (map #(char %) '(97 122 65 90))
       (apply str))
  ;; ok to recreate string

  (Character/isAlphabetic (int \a)) ;; true
  (Character/isAlphabetic (int \:)) ;; false
  (Character/isAlphabetic (int \0)) ;; false

  (+ 97 (mod 28 26))

  (->> (map #(if (Character/isAlphabetic (int %))
               (char (+ (int %) (mod 1 26))) ;; incorrect
               %) "ab,cd")
       (apply str))

  ;; the rotation function is not good
  )

(comment
  (let [c \b
        n -2
        shift (if (Character/isUpperCase c)
                65
                97)]
    (char (+ (mod (+ (- (int c) shift) n) 26) shift))))


(defn rot-char-n
  "rotate alphabetic character *c* on *n* positions and 
   returns the result char"
  [c n]
  (let [shift (if (Character/isUpperCase c) (int \A) (int \a))]
    (char (+ (mod (+ (- (int c) shift) n) 26) shift))))

(defn create-char-rotator [n]
  (fn [c]
    (if (Character/isAlphabetic (int c))
      (rot-char-n c n)
      c)))

(defn rotate [s n]
  (let [rotate-char-n (create-char-rotator n)]
    (->> (map rotate-char-n s)
         (apply str))))



(defn rotate-2 [s n]
  (->> s
       (map #(if (Character/isAlphabetic (int %))
               (rot-char-n % n)
               %))
       (apply str)))

