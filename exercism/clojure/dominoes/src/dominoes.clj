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
                      [:c2-2-2]
                   ]
               ]
              [:c3]
           ])
  (tree-seq (constantly true) next t1)
  )



(defn can-chain? [stones] ;; <- arglist goes here
  (cond
    (zero? (count stones))    true
    (= 1 (count stones))      (= (ffirst stones) (last (first stones)))
    :else false))
