(ns bob
  (:require [clojure.string :as str]))

(defn yelling? [s]
  (let [letter-only (apply str (re-seq #"[a-zA-Z]" s))]
    (if (= 0 (count letter-only))
      false
      (= letter-only (str/upper-case letter-only)))))

(defn asking? [s]
  (str/ends-with? (str/trim s) "?"))

(defn say-nothing? [s]
  (str/blank? s))

(defn response-for [s] ;; <- arglist goes here
  (cond
    (say-nothing? s) "Fine. Be that way!"
    (and (asking? s) (yelling? s)) "Calm down, I know what I'm doing!"
    (yelling? s) "Whoa, chill out!"
    (asking? s) "Sure."
    :else "Whatever."))

(comment
  (map char (range 97 123))
  (apply str (re-seq #"[a-zA-Z]" "A(B%A$c32d"))
  (response-for "AAA")
  (response-for "hello")
  (yelling? "1")
  (response-for "hello ? sdf")
  (response-for "WHAT ?")
  (response-for "")
  (bob/response-for "Ending with ? means a question."))
