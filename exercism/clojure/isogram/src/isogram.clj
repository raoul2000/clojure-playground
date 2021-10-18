(ns isogram
  (:require [clojure.string :refer [lower-case]]))

(defn isogram? [s]
  (->> (lower-case s)
       (frequencies)
       (filter #(Character/isLetter (first %)))
       vals
       (every? #(= 1  %))))

