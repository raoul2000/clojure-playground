(ns codewar.piglatin
  (:require [clojure.string :as string]))

;; https://www.codewars.com/kata/520b9d2ad5c005041100000f/train/clojure


(defn pig-word [s]
  (str (apply str (rest s)) (first s) "ay"))

(defn pig-it [s]
  (->> (string/split s #" ")
       (map #(if-not (Character/isLetter (first %))
               %
               (pig-word %)))
       (string/join " ")))


(comment
  (= (pig-it "Pig latin is cool") "igPay atinlay siay oolcay")
  (= (pig-it "This is my string") "hisTay siay ymay tringsay")

  ;; split a sentence in tokens
  (string/split "ab cd !" #" ")

  (let [s "latin"]
    (str (apply str (rest s)) (first s) "ay"))

  (string/join " " ["hello" "world"])


  ;;
  )