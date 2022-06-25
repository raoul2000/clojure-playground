(ns save)


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


(defn split-in-chunks [chunk-size s]
  (loop [i chunk-size, lst s, res []]
    (if (zero? i)
      res
      (recur (dec i)
             (rest lst)
             (conj res (into [] (take-nth chunk-size lst)))))))

(comment
  (split-in-chunks 3 "123456789")
  (split-in-chunks 4 "123456789a"))


(defn normalize-ciphertext [s]
  (let [ciphered-text (ciphertext s)
        [c r]         (compute-cxr (count ciphered-text))]
    (prn [c r ciphered-text])
    (->> (split-in-chunks r ciphered-text)
         ;;(map #(cond-> %
         ;;        (not= (count %) r) (conj ,, \space)))
         ;;(apply map vector)
         ;;(map #(apply str %))
         ;;
         ))) 


(comment
  (normalize-ciphertext "Vampires are people too!")
  (normalize-ciphertext "Madness, and then illumination.")

  (partition 3 3 "" "12345678")
  ;;
  )

(defn normalize-plaintext [s]
  (->> (filter #(Character/isLetterOrDigit %) s)
       (apply str)
       (clojure.string/lower-case)))

(defn square-size [s]
  (->> (count s)
       (Math/sqrt)
       (Math/ceil)
       (int)))


(defn plaintext-segments [s]
  (let [n (normalize-plaintext s)]
    (->> (partition-all (square-size n) n)
         (map #(apply str %)))))

(defn ciphertext [s]
  (loop [segs (plaintext-segments s) out []]
    (if (empty? segs)
      (apply str out)
      (recur
       (remove empty? (map rest segs))
       (into out (map first segs))))))


(defn normalize-ciphertext [s]
  (let [n (normalize-plaintext s) size (square-size n)]
    (->> (partition size size (cycle " ") n)
         (apply mapv str)
         (clojure.string/join " "))))