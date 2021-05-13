(ns four-clojure.flatten)





(defn my-flatten
  [s]
  (loop [l s
         result []]
    if( (empty? l)
       result
       ())
    )
  )

(comment
  (my-flatten '((1 2) 3 [4 [5 6]]))
  (my-flatten '((((:a)))))
  )