(ns four-clojure.flatten)

(defn my-flatten
  [subject]
  (loop [l subject]
    (if-not (some coll? l)
      l
      (recur (reduce #(into %1 (if (coll? %2) %2 (vector %2))) [] l)))))

(comment
  (my-flatten '((1 2) 3 [4 [5 6]]))
  (my-flatten '(1 2 3))
  (my-flatten ["a" ["b"] "c"])
  (my-flatten '((((:a))))))