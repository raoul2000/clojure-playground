(ns codewar.emirps)

;; https://www.codewars.com/kata/55de8eabd9bef5205e0000ba/train/clojure

(defn find-emirp [n]
  ; your code
  )

(comment
  (into [] (take 5 (range 2 100)))
  (into [] (repeat 10 true))
  (def candidates (into [] (repeat 10 true)))


  (Math/sqrt 10)
  (class (Math/sqrt 10))
  (->> (map-indexed #(when %2 %1) [true true true true false true false])
       (remove nil?))


  ;;
  )