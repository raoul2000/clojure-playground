(ns wordy
  (:require [clojure.string :refer [trim lower-case]]))

(def operator-symbol {"divided by"    "/"
                      "multiplied by" "*"
                      "plus"          "+"
                      "minus"         "-"
                      "what is"       "identity"})

(defn operator->symbol [s]
  (let [sym (operator-symbol s)]
    (if sym
      sym
      (throw (IllegalArgumentException. (format "invalid operator: %s" s))))))

(comment
  (operator->symbol "divided by")
  (operator->symbol "cube")

  ;;
  )

(defn tokenize 
  "Returns a seq of tokens where each token is a pair [operation operand]"
  [s]
  (if (re-matches #".*[a-zA-Z]+ *\??$" s)
    (throw (IllegalArgumentException.))
    (let [matcher (re-matcher #"([a-zA-Z ]+)(-?[0-9]+)" s)]
      (loop  [match (re-find matcher)
              result []]
        (if-not match
          result
          (let [res [(->> (second match)
                          trim
                          lower-case)
                     (last match)]]
            (recur  (re-find matcher)
                    (conj result res))))))))

(comment
  (tokenize "aaa dfg -98 eeez ze 98 ?")
  (tokenize "aaa dfg -98 eeez ze 98 operator C 55")
  (tokenize "What is 3 minus 4 eee")
  (tokenize "who is")
  (tokenize "What is -3 multiplied by 25?")
  (re-matches #".*[a-zA-Z]+ *\?$" "hello, world 3 ?")
  (throw (IllegalArgumentException.))
  ;;
  )

(defn tokens->sform [tokens]
  (reduce (fn r [acc [str-op str-val]]
            (format
             "(%s %s %s)"
             (operator->symbol str-op) acc str-val)) "" tokens))

(comment
  (tokens->sform [["op1" "1"] ["op2" "4"] ["op3" "5"]])
  (tokens->sform [["what is" "1"] ["divided by" "4"] ["minus" "5"]])
  (tokens->sform [["what is" "1"]])
  ;;
  )



(defn evaluate [s]
  (let [tokens (tokenize s)]
    (if (empty? tokens)
      (throw (IllegalArgumentException. ""))
      (->> (tokens->sform tokens)
           read-string
           eval))))

(comment
  (evaluate "What is -3 multiplied by 25")
  (evaluate "What is 56")
  (evaluate "What is 56 cubed?")

  ;;
  )

(comment
  (def matcher (re-matcher #"([a-zA-Z ]+)(-?[0-9]+)"
                           "What is 3 minus 4 cubed"))

  (re-find matcher)
  (re-groups matcher)

  ((comp trim lower-case) "E r ")
  ;;
  )
