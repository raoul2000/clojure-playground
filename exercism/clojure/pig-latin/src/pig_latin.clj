(ns pig-latin
  (:require [clojure.string :as str]))

(def vowels #{\a \e \i \o \u \A \E \I \O \U})

(defn is-vowel? [c]
  (boolean (get vowels c)))

(def is-consonant? (complement is-vowel?))

(defn starts-with-vowel-letter? [s]
  (is-vowel?  (first s)))

(def starts-with-consonant-letter? (complement starts-with-vowel-letter?))

(defn split-by-initial-consonant-cluster [s]
  [(apply str (take-while is-consonant? s))
   (apply str (drop-while is-consonant? s))])

(defn rule-1? [s]
  (or (starts-with-vowel-letter? s)
      (str/starts-with? s "xr")
      (str/starts-with? s "yt")))

(defn transform-1 [s]
  (str s "ay"))

(defn rule-2? [s]
  (starts-with-consonant-letter? s))

(defn transform-2 [s]
  (let [[consonant-cluster part-2] (split-by-initial-consonant-cluster s)]
    (str part-2 consonant-cluster "ay")))

(defn rule-3? [s]
  (and (starts-with-consonant-letter? s)
       (let [[consonant-cluster part-2] (split-by-initial-consonant-cluster s)]
         (and (str/ends-with? consonant-cluster "q")
              (str/starts-with? part-2 "u")))))

(defn transform-3 [s]
  (let [[consonant-cluster part-2] (split-by-initial-consonant-cluster s)]
    (str
     (apply str (rest part-2))    ;; remove first char 'u'
     (str consonant-cluster "u")  ;; concat 'u'
     "ay")))

(defn rule-4? [s]
  (or (let [[consonant-cluster part-2] (split-by-initial-consonant-cluster s)]
        (and
         consonant-cluster
         (str/starts-with? part-2 "y")))
      (and
       (= 2 (count s))
       (str/ends-with? s "y"))))

(defn transform-4 [s]
  (if (= 2 (count s))
    (str  "y" (first s) "ay")
    (let [[consonant-cluster part-2] (split-by-initial-consonant-cluster s)]
      (str part-2 consonant-cluster "ay"))))

(defn translate-word [s]
  (cond
    (rule-4? s) (transform-4 s)
    (rule-3? s) (transform-3 s)
    (rule-1? s) (transform-1 s)
    (rule-2? s) (transform-2 s)))

(defn translate [s]
  (str/join " " (map translate-word (str/split s #" "))))

