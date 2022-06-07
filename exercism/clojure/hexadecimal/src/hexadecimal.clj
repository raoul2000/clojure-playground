(ns hexadecimal)

(defn char-to-hex [c]
  (cond
    (Character/isDigit c)    (Character/digit c 10)
    (#{\a \b \c \d \e \f} c) (- (int c) 87)))

(comment
  (char-to-hex \1)
  (char-to-hex \a)
  (char-to-hex \f)
  (map char-to-hex "10")
  (map char-to-hex "20")
  (map char-to-hex "f0")
  [1 1 0]

  ;; 1 0
  ;; 16x1 + 0    = 16
  ;; 2         0
  ;; 16x2 + 0    = 32
  ;; F         0
  ;; 16x15 + 0  = 
  ;; 1         0      0
  ;; 16x16x1
  ;; 3         2      0
  ;; 16x16x3 + 16x2 + 0 =  

  ;; [ F 2 0]
  ;; [ 16^3  16^2  16^0]
  (map #(* %1 %2) [0 2 3] (iterate #(* 16 %) 1))
  (apply + (map #(* %1 %2) [0 2 3] (iterate #(* 16 %) 1)))
  (apply + (map #(* %1 %2)  (iterate #(* 16 %) 1) [0 2 3])))

(def hex-char (zipmap "0123456789abcdef" (range)))

(defn hex-to-int [hexa-str]
  (let [dec-pos (map hex-char hexa-str)]
    (cond
      (some nil? dec-pos)  0
      :else                (->> dec-pos
                                reverse
                                (map #(* %1 %2) (iterate #(* 16 %) 1))
                                (apply +)))))

;; could also use zipmap
(comment
  (map char-to-hex "0123456789abcdef")
  (def hex-char (zipmap "0123456789abcdef" (range)))
  (map hex-char "ab")
  )

