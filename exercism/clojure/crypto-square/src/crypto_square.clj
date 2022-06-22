(ns crypto-square
  (:require [clojure.string :refer [lower-case]]))

(defn normalize-plaintext [s]
  (->> s
       (filter #(Character/isLetterOrDigit %))
       (map lower-case)
       (apply str)))

(comment
  (normalize-plaintext "abc")
  (normalize-plaintext "@e#abc")
  ;;
  )

;; r x c >= (count s)
;; c >= r
;; c - r <= 1

(defn row-count
  "Returns `r` the number of rows where `r * c >= v`"
  [v c]
  (cond-> (quot v c)
    (pos-int? (rem v c)) (inc)))

(comment
  (row-count 11 5)
  (mod 11 5))

(defn square-size-invalid? [[c r]]
  (not (and (>= c r)
            (<= (- c r) 1))))

(defn square-size-1 [s]
  (let [str-len (count s)]
    (->> (range 1 str-len)
         (map #(vector % (row-count str-len %)))
         (take-while square-size-invalid?)
         ((comp inc first last)))))

(comment
  (row-count 54 7)

  (take 10 (map #(quot 54 %) (range 1 54)))
  (take 10 (map #(vector % (quot 54 %)) (range 1 54)))

  (->> (range 1 12)
       (map #(vector % (row-count 12 %)))
       (take-while square-size-invalid?)
       ((comp inc first last)))
  ;;
  )

(defn rxc-valid? [c r len]
  (and (>= c r)
       (>= (* c r) len)
       (<= (- c r) 1)))

(defn compute-rxc [l]
  (loop [c 1]
    (let [r (quot l c)]
      (if (rxc-valid? c r l)
        [c r]
        (recur (inc c))))))

(comment
  (compute-rxc 4)
  (compute-rxc 3)
  (compute-rxc 9)
  (compute-rxc 10)
  (compute-rxc 12)
  (compute-rxc 11) 
  (count (normalize-plaintext "ZOMG! ZOMBIES!!!"))
  ;;
  )

(defn square-size [s]
  (first (compute-rxc (count s))))

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

(comment
  (def s1 "Time is an illusion. Lunchtime doubly so.")
  (plaintext-segments s1)
  (ciphertext s1)
  (apply map vector [[:1 :2 :3] [:a :b :c]  [:R :T :V]])

  (apply map vector '((\t \i \m \e \i \s)
                      (\a \n \i \l \l \u)
                      (\s \i \o \n \l \u)
                      (\n \c \h \t \i \m)
                      (\e \d \o \u \b \l)
                      (\o \s \y nil nil nil nil nil nil)))
  (map seq ["timeis" "anillu" "sionlu" "nchtim" "edoubl" "yso"])

  (mapcat #(cond-> (apply vector (seq %))
             (< (count %) 6) (into (repeat 6 nil)))
          '("timeis" "anillu" "sionlu" "nchtim" "edoubl" "yso"))

  (map vector [:1 :2 :3] [:a :b :c] (into [:R :T] (repeat 10 nil)))
  (mapcat vector [:1 :2 :3] [:a :b :c] (into [:R :T] (repeat 10 nil)))

  ;;
  )
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
