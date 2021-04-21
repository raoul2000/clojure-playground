(ns bob
  (:require [clojure.string :as str]))

(defn yelling? [s]
  (and 
   (re-matches #".*[a-zA-Z]+.*" s)
   (= s (str/upper-case s)))
  )

(defn yelling-1? [s]
  (let [letter-only (apply str (re-seq #"[a-zA-Z]" s))]
    (if (= 0 (count letter-only))
      false
      (= letter-only (str/upper-case letter-only)))))

(defn asking? [s]
  (str/ends-with? (str/trim s) "?"))

(def say-nothing? str/blank?)

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
  (response-for "WHAT dd 3 ?")
  (response-for "")
  (bob/response-for "Ending with ? means a question."))
