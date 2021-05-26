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
  (= (inters #{:a :b :c :d} #{:c :e :a :f :d}) #{:a :c :d}))

;; problem/90
;; Write a function which calculates the Cartesian product of two sets.

(defn cart [s1 s2]
  (set (for [c2 (sort s2)
             c1 (sort s1)]
         [c1 c2])))

(comment
  (= (cart #{1 2 3} #{4 5})
     #{[1 4] [2 4] [3 4] [1 5] [2 5] [3 5]})

  (= 300 (count (cart (into #{} (range 10))
                      (into #{} (range 30)))))

  (for [a (sort #{4 5})
        b (sort #{1 2 3})]
    [b a]))