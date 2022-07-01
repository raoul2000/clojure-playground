(ns dominoes)


;; represent a tree data structure
;; [node1 node1.1 node1.2] : parent = first, children = rest

;; node1
;;  +---- node1.1
;;          +---- node1.1.1
;;          +---- node1.1.2
;;  +---- node1.2
;;          +---- node1.2.1
;;          +---- node1.2.2
;;          +---- node1.2.3
;;                   +---- node1.2.3.1
;;                   +---- node1.2.3.2
;; model : an array of arrays ..
;; [node1 
;;      [node1.0]
;;      [node1.1 
;;         node1.1.1 
;;         node1.1.2]
;;      [node1.2 
;;         node1.2.1 
;;         node1.2.2 
;;        [node1.2.3 
;;              node1.2.3.1 
;;              node1.2.3.2]]
;;]

;; [root]   1,2 1,3 2,3
;; [root 
;      [1,2] 
;;     [2,1]]        
;; --------------
;; [root 
;      [1,2 
;;        [2,3]] 
;;     [2,1 
;;        [1,3]]]        
;; --------------
;;
;; [root 
;      [1,2 
;;        [2,3
;;           [3,1]]] 
;;     [2,1 
;;        [1,3
;;           [3,2]]]]        
;; --------------
(comment
  (rest [1 2 3])
  (next [1 2 3])
  (take-while seq (iterate next [1 2 3]))
  (take-while seq (iterate (comp first next) [1 [2 [3]]])) ;; 3 the leaf
  (take-while seq (iterate (comp first next) [1 [2 [3]]
                                              [:a [:b [:c]]]]))
  (def t1 [:root
           [:c1]
           [:c2
            [:c2-1]
            [:c2-2
             [:c2-2-1]
             [:c2-2-2]]]
           [:c3]])
  (tree-seq (constantly true) next t1)
  (def t2 {:root {:c1 nil
                  :c2 {:c2-1 nil
                       :c2-2 nil
                       :c2-3 {:c2-3-1 {:c2-3-1-1 nil
                                       :c2-3-1-2 nil}}}}})

  (tree-seq (comp nil? second) second {:A {:A1 nil
                                           :A2 {:A2-1 nil}}})
  ;;
  )

(def create-node vector)
(def is-node?   vector?)

(defn add-child [child node]
  (conj node (if (is-node? child) child (create-node child))))

(comment
  (->> (create-node :root)
       (add-child :r)
       (add-child (->> (create-node :c1)
                       (add-child :d)
                       (add-child (->> (create-node :N1)
                                       (add-child :N2)))))
       (add-child :2))
  ;;
  )

(defn domino-match? [n [d1 d2]]
  (or (= n d1)
      (= n d2)))

(comment
  (domino-match? 1 [1 2])
  (domino-match? 2 [1 2])
  (domino-match? 1 [2 3])

  (group-by (partial domino-match? 1)  [[1 2] [1 3] [4 3] [1 5]])
  

  ;;
  )


(defn can-chain? [stones] ;; <- arglist goes here
  (cond
    (zero? (count stones))    true
    (= 1 (count stones))      (= (ffirst stones) (last (first stones)))
    :else false))
