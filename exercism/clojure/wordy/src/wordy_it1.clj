(ns wordy-it1)

(def operator-symbol {"divided by"    "/"
                      "multiplied by" "*"
                      "plus"          "+"
                      "minus"         "-"})

(defn operator->symbol
  "Returns mathematical symbole matching s or throws when
   no match is found"
  [s]
  (let [sym (operator-symbol s)]
    (if sym
      sym
      (throw (IllegalArgumentException. (format "invalid operator: %s" s))))))

(comment
  (operator->symbol "divided by")
  (operator->symbol "cube")
  ;;
  )

(defn tokenize-operations [s]
  (let [matcher     (re-matcher #"((?<op>divided by|multiplied by|plus|minus) +(?<val>-?[0-9]+) *)" s)
        first-match (re-find matcher)]
    (if (and (> (.length s) 0) (empty? first-match))
      (throw (IllegalArgumentException. (format "invalid operator: %s" s)))
      (loop  [match first-match
              result []]
        (if-not match
          result
          (let [res  {:operator (operator->symbol (.group matcher "op"))
                      :operand  (.group matcher "val")}]
            (recur  (re-find matcher)
                    (conj result res))))))))

(comment
  (tokenize-operations "multiplied by 2 divided by 4 minus 5")
  (tokenize-operations "plus 2")
  (tokenize-operations "cubed 2")
  (tokenize-operations "what is 2?")
  (eval '(+ 1 1))
  ;;
  )

(defn tokenize [s]
  (when-let [[_ str-val str-operations] (re-find (re-matcher #"^What is (-?[0-9]+) *(..+)*\?$" s))]
    [str-val (and
              str-operations
              (tokenize-operations str-operations))]))

(comment
  (tokenize "What is 4 minus 3 multiplied by 10?")
  (tokenize "What is 4 ?")
  (tokenize "What is  ?")
  (tokenize "Who is  ?")
  ;;
  )



(defn parse [s]
  (when-let [[str-init-val operations] (tokenize s)]
    (loop [ops    operations
           result (format "(identity %s)" str-init-val)]
      (if (empty? ops)
        result
        (recur
         (rest ops)
         (format "(%s %s %s)"
                 (:operator (first ops))
                 result
                 (:operand (first ops))))))))

(comment

  (parse "What is 4 minus 3 multiplied by 10?")
  (eval (read-string "(* (- (identity 10) 3) 1)"))
  (parse "What is 4 cubed?")
  (parse "What is 4?")
  (parse "qWhat is 4 minus 3 multiplied by 10?")

  ;;
  )

(defn evaluate [s] ;; <- arglist goes here
  (let [str-eval (parse s)]
    (if str-eval
      (eval (read-string str-eval))
      (throw (IllegalArgumentException. "")))))

(comment

  (evaluate "What is 4 minus 3 multiplied by 10?")
  (evaluate "What is -25?")
  (evaluate "What is 2 multiplied by -2 multiplied by 3?")

  ;;
  )



(comment
  (def re #"((?<op>multiplied by|divided by) +(?<val>-?[[:digit:]]+))")
  (def re (re-pattern "((?<op>divided by) +(?<val>-?[[:digit:]]+))"))

  (def mm (re-matcher #"What is (-?[0-9]) +(.*)\?" "What is 5 plus 4 minus 5?"))
  (re-find mm)





  (def matcher (re-matcher #"What is 3 (?<op>divided by|multiplied by|plus) +(?<val>-?[0-9])"
                           "What is 3 multiplied by 2 divided by 4"))

  (re-find matcher)
  ;;(re-groups matcher)
  (def operation {:operator (.group matcher "op")
                  :operand  (.group matcher "val")})
  (prn operation)
  (.group matcher "op")
  (.group matcher "val")


  (loop [match (re-find matcher)
         result []]
    (if-not match
      result
      (let [res {:operator (.group matcher "op")
                 :operand  (.group matcher "val")}]
        (recur (re-find matcher)
               (conj result res)))))


  (loop [match (re-find matcher)
         result []]
    (if-not match
      result
      (recur (re-find matcher)
             (conj result match))))




  (def matcher (re-matches re "divided by -2"))
  (re-find matcher)
  (.group matcher "op")

  (def re2 #"((?:az|rt))")
  (def matcher2 (re-matcher re2 "azazrt"))
  (re-find matcher2)
  ;;
  )