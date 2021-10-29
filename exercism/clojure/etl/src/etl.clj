(ns etl
  (:require [clojure.string :refer [lower-case]]))

(defn transform [source]
  (into {} (for [[num words-list] source
                 word words-list]
             [(lower-case word) num])))
