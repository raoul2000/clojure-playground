(ns crypto-square
  (:require [clojure.string :refer [lower-case]]))

(defn normalize-plaintext [s]
  (->> s
       (filter #(Character/isLetterOrDigit %))
       (map lower-case)
       (apply str)))

(defn compute-cxr [len]
  (let [c (Math/round (Math/sqrt len))]
    (cond
      (>= (* c c) len) [c       c]
      :else            [(inc c) c])))

(defn square-size [s]
  (first (compute-cxr (count s))))


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
  (let [ciphered-text (ciphertext s)
        col-count     (square-size ciphered-text)
        chunk-len     (quot (count ciphered-text) col-count)]
    (partition col-count col-count [" "] ciphered-text)))

(comment

  (normalize-ciphertext "Vampires are people too")
  (normalize-ciphertext "Madness, and then illumination.")
  (square-size "vrelaepemsetpaooirpo")
  (count "vrelaepemsetpaooirpo")
  (partition-all 5 "vrelaepemsetpaooirpo")

  (square-size (normalize-plaintext "Madness, and then illumination."))
  ;;
  )
