(ns crypto-square
  (:require [clojure.string :refer [lower-case join]]))

(defn normalize-plaintext [s]
  (->> s
       (filter #(Character/isLetterOrDigit %))
       (map lower-case)
       (apply str)))

(defn square-size [s]
  (->> (count s)
       (Math/sqrt)
       (Math/ceil)
       (int)))

(defn plaintext-segments [s]
  (let [normalized-text (normalize-plaintext s)
        segment-len     (square-size normalized-text)]
    (->> (partition-all segment-len normalized-text)
         (map #(apply str %)))))

(defn ciphertext [s]
  (let [segments    (plaintext-segments s)
        segment-len (count (first segments))]
    (->> segments
         (map #(cond-> (apply vector (seq %))
                 (< (count %) segment-len) (into ,,, (repeat segment-len nil))))
         (apply mapcat vector)
         (remove nil?)
         (apply str))))

(defn normalize-ciphertext [s]
  (let [n (normalize-plaintext s) size (square-size n)]
    (->> (partition size size (repeat " ") n)
         (apply mapv str)
         (join " "))))