(ns zip-play
  (:require [clojure.zip :as z]))

(comment

  ;; create a zipper function to build the tree data structure
  ;; In our case each node will be a map with 2 keys:
  ;; - :content : stores node'es private data
  ;; - :children : a possibly empty seq of children nodes
  ;; 
  ;; create the root-location 
  (def root-loc (z/zipper
                 (constantly true)           ;; branch? : all nodes may have children
                 #(:children %)              ;; children : children nodes are at :children key
                 #(assoc %1 :children %2)    ;; make-node
                 {:content [0] :children []} ;; the root node
                 ))
  ;; let's see the result
  (z/root root-loc)
  ;; get the leftmost children of the root location
  (z/node (z/down root-loc))
  ;; => nil no children

  ;; append a node to the root location and get a new root-location
  (def new-root-loc (z/append-child root-loc {:content [3] :children []}))

  (z/node (z/down new-root-loc))
  ;; => {:content [3], :children []}

  ;; again append another node ... and 
  (def root-loc-2 (z/append-child new-root-loc {:content [4] :children []}))
  (z/node (z/down root-loc-2))
  ;; => {:content [3], :children []}
  ;; we get the same result as before, because z/down, returns the LEFTMOST child of the 
  ;; given node location.

  ;; How can we get the node with content = 4 ?
  ;; Use z/right to get the location of the right sibling from the current location
  (z/node (-> root-loc-2
              z/down
              z/right)) ;; => {:content [4], :children []}

  ;; to get ALL children, use z/children
  (z/children root-loc-2)  ;; => ({:content [3], :children []} {:content [4], :children []})


  ;; let's Add 2 children to the node 4. First navigate the tree from the root location
  ;; to the node 4 location. Then add children in threaded way because z/append-child does not
  ;; change the location
  (def loc-3 (-> (-> root-loc-2
                     z/down
                     z/right)   ;; move to node 4
                 (z/append-child {:content "ZZ" :children []})  ;; add child and preserve location
                 (z/append-child {:content "XX" :children []})  ;; add nother child (same location) 
                 ))
  ;; the previous append children operation returned the location 
  ;; of the node 4
  (z/node loc-3)

  ;; we want to go back to the root location so navigate one level up
  (def root-loc-3 (-> loc-3 z/up))
  (z/node root-loc-3)


  (-> root-loc-3 z/children)

  ;;
  )


(comment
  ;; using vector zip. The data structure in a nested vector
  ;; where vectors are branch and non-vector are leaf
  ;;    .
  ;;   / \
  ;;  1   .
  ;;    / | \
  ;;   11 12 13
  (def root-loc (z/vector-zip [1 [11 12 13]]))

  (z/node root-loc)
  (z/node (-> root-loc z/down)) ;; => 1
  (z/node (-> root-loc z/down z/next z/down)) ;; => 11

  (z/node (-> root-loc z/next))                                   ;; 1
  (z/node (-> root-loc z/next z/next))                             ;; [11 12 13]
  (z/node (-> root-loc z/next z/next z/next))                      ;; 11
  (z/node (-> root-loc z/next z/next z/next z/next))               ;; 12
  (z/node (-> root-loc z/next z/next z/next z/next z/next))        ;; 13
  (z/node (-> root-loc z/next z/next z/next z/next z/next z/next)) ;; [1 [11 12 13]]
  (-> root-loc z/next z/next z/next z/next z/next z/next z/end?)   ;; true
  ;;
  )

(comment
  ;; let's define our own data structure to represent tree
  ;; a node : {:node "whatever" :children []}
  (def zipper-tree (partial z/zipper
                            (constantly true)
                            :children
                            #(assoc %1 :children %2)))

  (defn create-node [data]
    {:node data :children []})

  ;; let's create the following tree
  ;;
  ;; root
  ;;   +--- 1
  ;;        +--- 1.1
  ;;   +--- 2
  ;;        +--- 2.1
  ;;              +--- 2.1.1
  ;;              +--- 2.1.2
  ;;                       
  (def root-loc (zipper-tree  (create-node "root")))


  (def r2-loc (zipper-tree (-> root-loc
                               (z/append-child (create-node "1"))
                               (z/append-child (create-node "2"))
                               z/down
                               (z/append-child (create-node "1.1"))
                               z/right
                               (z/append-child (create-node "2.1"))
                               z/down
                               (z/append-child (create-node "2.1.1"))
                               (z/append-child (create-node "2.1.2"))
                               z/root)))

  (z/node r2-loc)
  ;; lets navigate this tree starting from r2-location
  (-> r2-loc z/down z/down) ;; 1.1
  (-> r2-loc z/down z/right z/down z/down z/right) ;; 2.1.2

  ;; use z/next to navigate from a location in a deep-first way
  (-> r2-loc z/next z/next z/next z/next)

  ;; with a loop, navigate all the tree
  (loop [loc r2-loc]
    (if (z/end? loc)
      "done"
      (do
        (println (:node (z/node loc)))
        (recur (z/next loc)))))

  ;; same loop but this time, edit each visited node : add an index in front of
  ;; node value
  (loop [loc r2-loc
         cnt 0]
    (if (z/end? loc)
      (z/root loc)
      (recur (z/next (z/edit loc #(update % :node (partial str cnt "-"))))
             (inc cnt))))
  ;;
  )

(comment
  ;; let's go back to the Dominoes exercism
  ;; each node contains :
  ;; - a domino represented as a vecotr of 2 integers
  ;;      - (first dom) : match the parent number
  ;; 

  ;;
  )