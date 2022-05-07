(ns flatten-array
  (:refer-clojure :exclude [flatten])
  (:require [clojure.core :as c]))

(def flat clojure.core/flatten)

(comment
  (flat [1 2 [4]]))

(defn flatten1 [arr] ;; <- arglist goes here
  (loop [ar arr
         result []]
    (if  (empty? ar)
      (remove nil? result)
      (recur (rest ar)
             (into result (if (vector? (first ar))
                            (flatten-array/flatten (first ar))
                            [(first ar)]))))))

(defn flatten [arr]
  (->> (tree-seq sequential? identity arr)
       (remove #(or (sequential? %)
                    (nil? %)))))

(comment
  (flatten-array/flatten [1 [:a :b]])

  (flatten-array/flatten [0 2 [[2 3] 8 100 4 [[[50]]]] -2 nil])
  (flatten [nil [[[nil]]] nil nil [[nil nil] nil] nil])

  (->> (tree-seq sequential? identity [1 2 [:a :b]])
       (remove sequential?))

  ;;
  )
