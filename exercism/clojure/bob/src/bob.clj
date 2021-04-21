(ns bob
  (:require [clojure.string :as str]))

(defn yelling?
  "true if contains alpha chars and they are all upper case"
  [s]
  (and
   (re-matches #".*[a-zA-Z]+.*" s)
   (= s (str/upper-case s))))

(defn asking?
  "true if ends with '?"
  [s]
  (str/ends-with? (str/trim s) "?"))

(def say-nothing? str/blank?)

(defn response-for [s]
  (cond
    (say-nothing? s) "Fine. Be that way!"
    (and (asking? s) (yelling? s)) "Calm down, I know what I'm doing!"
    (yelling? s) "Whoa, chill out!"
    (asking? s) "Sure."
    :else "Whatever."))
