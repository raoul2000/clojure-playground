(ns secret-handshake)

;; create a list of pair where first item is the mask value in decimal
;; and the secopnd is the secret string

(def v [[1 "wink"]
        [2 "double wink"]
        [4 "close your eyes"]
        [8 "jump"]
        [16 "reverse"]])

;; reduce the secret map
(comment
  (reduce (fn[acc [i s]]
            (if-not (zero? (bit-and 19 i))
              (conj acc s)
              acc)) [] v)
)

;; let's try using cond->


(defn commands [n] 
  (cond-> []
    (bit-test n 0 ) (conj "wink")
    (bit-test n 1 ) (conj "double blink")
    (bit-test n 2 ) (conj "close your eyes")
    (bit-test n 3 ) (conj "jump")
    (bit-test n 4 ) (reverse)))

