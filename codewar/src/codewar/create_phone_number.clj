(ns create-phone-number)

;; https://www.codewars.com/kata/525f50e3b73515a6db000b83/train/clojure
;; (create-phone-number [1 2 3 4 5 6 7 8 9 0]) ;; => returns "(123) 456-7890"

(defn create-phone-number [num]
  (let [code (apply str (take 3 num))
        part-1 (apply str (take 3 (drop 3 num)))
        part-2 (apply str (drop 6 num))]
    (str "(" code ") " part-1 "-" part-2)))



(comment
  (create-phone-number [1 2 3 4 5 6 7 8 9 0])

  (apply str (take 3 [1 2 3]))
  (apply str (take 3 (drop 3 [1 2 3 4 5 6 7])))
  ;;
  )


