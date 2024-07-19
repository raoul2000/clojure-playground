(ns codewar.meeting
  (:require [clojure.string :refer [split upper-case join]]))


;; https://www.codewars.com/kata/59df2f8f08c6cec835000012/train/clojure

(defn full-name [[f-name l-name]]
  (str l-name f-name))

(defn merge-names [[f-name l-name]]
  (format "(%s, %s)" l-name f-name))

(defn meeting [s]
  (->> s
       upper-case
       (re-seq #"([a-zA-Z]+):([a-zA-Z]+)")
       (map rest)
       (sort-by full-name)
       (map  merge-names)
       join))

(comment



  (def list1 "Alexis:Wahl;John:Bell;Victoria:Schwarz;Abba:Dorny;Grace:Meta;Ann:Arno;Madison:STAN;Alex:Cornwell;Lewis:Kern;Megan:Stan;Alex:Korn")
  (def list2 "John:Gates;Michael:Wahl;Megan:Bell;Paul:Dorries;James:Dorny;Lewis:Steve;Alex:Meta;Elizabeth:Russel;Anna:Korn;Ann:Kern;Amber:Cornwell")


  (def list3 "bb:cc;zz:ee;bb:aa")


  (meeting list2)


  (map  #(split % #":")  (split (upper-case list1) #";"))

  (def l2 (map rest (re-seq #"([a-zA-Z]+):([a-zA-Z]+)" (upper-case list3))))


  (sort-by #(apply str %) l2)





  (->> list
       (split list))



  ;;
  )