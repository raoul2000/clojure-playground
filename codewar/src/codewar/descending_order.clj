(ns codewar.descending-order)

;; https://www.codewars.com/kata/5467e4d82edf8bbf40000155/train/clojure

(defn desc-order [n]
  (->> n
       ((comp reverse sort str))
       (apply str)
       (Integer/parseInt)))

(comment

  (desc-order 42145)
  (map identity "1236")
  (sort (map #(Character/digit % 10) "1236"))
  ((comp  reverse sort) (map #(Character/digit % 10) "1236"))
  (apply str ((comp  reverse sort) (map #(Character/digit % 10) "1236")))

  (apply str (reverse (sort "42145")))
  (apply str ((comp reverse sort) "42145"))
  (map identity "1236")
  (str 1254)
  (Character/valueOf \2)

  (Integer/parseInt "11254")
  ;;
  )