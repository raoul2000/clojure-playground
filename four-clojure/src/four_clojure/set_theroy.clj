(ns four-clojure.set-theory)

;; Write a function which returns the intersection of two sets. The intersection is the sub-set of items that each set has in common.

(defn inters [l1 l2]
  (set (filter identity (for [s1 l1
                              s2 l2]
                          (if (= s1 s2)
                            s1)))))


(comment
  (= (inters #{0 1 2 3} #{2 3 4 5}) #{2 3})
  (= (inters #{0 1 2} #{3 4 5}) #{})
  (= (inters #{:a :b :c :d} #{:c :e :a :f :d}) #{:a :c :d})
  )