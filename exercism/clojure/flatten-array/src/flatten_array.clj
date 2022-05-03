(ns flatten-array
  (:require [clojure.core :as c]))

(def flat clojure.core/flatten)

(comment
  (flat [1 2 [4]]))

(defn flatten [arr] ;; <- arglist goes here
  (loop [ar arr
         result []]
    (if  (empty? ar)
      (remove nil? result)
      (recur (rest ar)
             (into result (if (vector? (first ar))
                            (flatten-array/flatten (first ar))
                            [(first ar)]))))))

(comment
  (flatten-array/flatten [1 [:a :b]])
  (flatten-array/flatten [0 2 [[2 3] 8 100 4 [[[50]]]] -2 nil])
  (flatten [nil [[[nil]]] nil nil [[nil nil] nil] nil])
  ;;
  )
