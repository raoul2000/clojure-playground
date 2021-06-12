(ns isbn-verifier
  (:require [clojure.string :as s]))

(defn isbn-char->int
  [c]
  (if (= \X c)
    10
    (Character/digit c 10)))

(defn isbn? [isbn]
  (let [no-dash (re-matches #"^\d{9}[X\d]$" (s/replace isbn "-" ""))]
    (and
     (some? no-dash)
     (->> no-dash
          (map isbn-char->int)
          (map * (range 10 0 -1))
          (apply +)
          (#(mod % 11))
          zero?))))