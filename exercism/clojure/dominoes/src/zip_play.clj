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

  (z/node (-> root-loc z/next ))                                   ;; 1
  (z/node (-> root-loc z/next z/next))                             ;; [11 12 13]
  (z/node (-> root-loc z/next z/next z/next))                      ;; 11
  (z/node (-> root-loc z/next z/next z/next z/next))               ;; 12
  (z/node (-> root-loc z/next z/next z/next z/next z/next))        ;; 13
  (z/node (-> root-loc z/next z/next z/next z/next z/next z/next)) ;; [1 [11 12 13]]
  (-> root-loc z/next z/next z/next z/next z/next z/next z/end?)   ;; true



  )