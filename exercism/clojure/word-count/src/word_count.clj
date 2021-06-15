(ns word-count
  (:require [clojure.string :refer [lower-case]]))

(defn word-count [s]
  (->> (lower-case s)
       (re-seq #"[\w]+")
       (frequencies)))
