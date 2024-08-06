(ns codewar.meeting
  (:require [clojure.string :refer [split upper-case join]]))


;; https://www.codewars.com/kata/59df2f8f08c6cec835000012/train/clojure

(def last-name-and-first-name (juxt second first))

(defn merge-names [[f-name l-name]]
  (format "(%s, %s)" l-name f-name))

(defn meeting [s]
  (->> s
       upper-case
       (re-seq #"([A-Z]+):([A-Z]+)")
       (map rest)
       (sort-by last-name-and-first-name)
       (map  merge-names)
       join))

(comment

  (def list1 "Alexis:Wahl;John:Bell;Victoria:Schwarz;Abba:Dorny;Grace:Meta;Ann:Arno;Madison:STAN;Alex:Cornwell;Lewis:Kern;Megan:Stan;Alex:Korn")
  (def list2 "John:Gates;Michael:Wahl;Megan:Bell;Paul:Dorries;James:Dorny;Lewis:Steve;Alex:Meta;Elizabeth:Russel;Anna:Korn;Ann:Kern;Amber:Cornwell")

  (meeting list2)
  (meeting "Robert:Russel;Haley:Russell")
  (meeting "DDD:BBB;AAA:BBB")


  (map  #(split % #":")  (split (upper-case list1) #";"))

  (def l2 (map rest (re-seq #"([a-zA-Z]+):([a-zA-Z]+)" (upper-case list3))))


  (sort-by #(apply str %) l2)
  ;;
  )