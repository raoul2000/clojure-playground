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





(defn can-chain? [stones] ;; <- arglist goes here
  (cond
    (zero? (count stones))    true
    (= 1 (count stones))      (= (ffirst stones) (last (first stones)))
    :else false))
