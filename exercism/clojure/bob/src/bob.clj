(ns bob
  (:require [clojure.string :as str]))

(defn yelling?
  "true if contains alphabetic characters and they are all upper case"
  [s]
  (let [letter-only (apply str (re-seq #"[a-zA-Z]" s))]
    (if (= 0 (count letter-only))
      false
      (= letter-only (str/upper-case letter-only)))))

(defn asking?
  "true if ends with '?"
  [s]
  (str/ends-with? (str/trim s) "?"))

(defn say-nothing?
  "true if blank"
  [s]
  (str/blank? s))

(defn response-for [s]
  (cond
    (say-nothing? s) "Fine. Be that way!"
    (and (asking? s) (yelling? s)) "Calm down, I know what I'm doing!"
    (yelling? s) "Whoa, chill out!"
    (asking? s) "Sure."
    :else "Whatever."))
