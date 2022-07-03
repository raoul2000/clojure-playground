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


  ;; let's go back to the Dominoes exercism
  ;; - a domino represented as a vector of 2 integers
  ;; - 2 dominoes can connect to each other if they have at least one value in common
  ;;      example: [1 2] and [1 3] can connect as [2 1] [1 3]. The domino [1 2] has been flipped
  ;; - a domino chain is a list of more than 1 domino, where each domino is connected to another one
  ;; except for the first and the last domino of the chain
  ;;      Example: [ [1 2] [2 4] [4 4] [4 2]]
  ;; - a domino chain is valid if the 2 not connected values are the same

(defn flip [[v1 v2]]  [v2 v1])


(defn can-connect-no-flip [[_ a2] [b1 _]] (= a2 b1))

(comment
  (flip [2 3])
  (can-connect-no-flip [1 2] [2 1])
  (can-connect-no-flip [1 2] [3 1])
  (can-connect-no-flip [1 2] [3 4]))

(defn connect
  "Given 2 dominoes, where the first one is 'fixed' (i.e can't flip) and the second one is 
     a candidate for connection, returns nil if they can't connect or the candidate dominoe
     possibly fillped to connect to the first one."
  [fixed-domino domino]
  (if (can-connect-no-flip fixed-domino domino)
    domino
    (let [flipped-b (flip domino)]
      (when (can-connect-no-flip fixed-domino flipped-b)
        flipped-b))))

(comment
  (connect [1 2] [2 3])
  (connect [1 2] [3 2])
  (connect [1 2] [3 4]))

(defn group-by-connect [tail-domino dominoes]
  (reduce (fn [result domino]
            (if-let [connected-dom (connect tail-domino domino)]
              (update result :can-connect conj connected-dom)
              (update result :rejected    conj domino)))
          {:can-connect [] :rejected []}
          dominoes))

(comment
  (group-by-connect [1 2] [[1 2] [2 1] [3 4] [4 3]])
  (group-by-connect [1 4] [[1 4] [2 1] [3 4] [4 3]])
  (group-by-connect [6 7] [[1 4] [2 1] [4 4] [4 3]])
  (group-by-connect [1 4] [[1 4] [2 1] [4 4] [4 3]]))

  ;; this is ok but it would be better to get for each match, the matching domino and the remainng dominoes
  ;; Example:
  ;; (matches [1 2] [ [1 2] [2 1] [3 4] [4 3]])
  ;; => [
  ;;      [ [2 1] 
  ;;             [[2 1] [3 4] [4 3]] 
  ;;      ]
  ;;      [ [2 1] 
  ;;             [[1 2] [3 4] [4 3]] 
  ;;      ]
  ;;   ]
  ;;]



(defn remove-domino-by-index [idx indexed-dominoes]
  (->> (remove (fn [[current-idx _]]
                 (= current-idx idx))
               indexed-dominoes)
       (map last)  ;; ignore first item (the index) only keep domino
       ))

(comment
  (remove-domino-by-index 2 [[1 :a] [2 :b] [3 :c]])
  (remove-domino-by-index 2 [[1 :a] [4 :b] [3 :c]]))

(defn find-possible-connections
  "Given a fixed domino (can't flip) and a list of dominoes candidates to connect,
     Returns a vector of all possible connections, where the first item is the possibly flipped
     domino that connects, and the second item is the list of remaining dominoes.
     Returns an empty vector when no connection is possible. "
  [fixed-domino dominoes]
  (let [indexed-dominoes (map-indexed vector dominoes)]
    (reduce (fn [result [idx domino]]
              (if-let [connected-domino (connect fixed-domino domino)]
                (conj result [connected-domino (remove-domino-by-index idx indexed-dominoes)])
                result))
            []
            indexed-dominoes)))

(comment
  (find-possible-connections [1 2] [[3 2] [2 1] [3 4] [4 3]])
  ;; => [  [[2 3] ,  ([2 1] [3 4] [4 3])] 
  ;;       [[2 1] ,  ([3 2] [3 4] [4 3])]
  ;; ]
  (find-possible-connections [1 2] [[2 6] [5 2] [3 4] [4 3]])
  (find-possible-connections [1 2] [[3 6] [5 4] [3 4] [4 3]])
  ;; => []
  )


  ;; Now lets  prepare our tree
  ;; each node has the following shape
  ;; {
  ;;    :domino [1 2]
  ;;    :remain [[2 4] [5 5] [4 3]]
  ;;    :last true 
  ;;    :children []
  ;; }
  ;;
  ;; create the zipper
(def zipper-domino-chain (partial z/zipper
                                  (constantly true)
                                  :children
                                  #(assoc %1 :children %2)))

(defn create-domino-node [domino remain]
  {:domino domino :remain remain :children []})

(comment
    ;; First we will build manually the tree
  ;; create the root location
  (def root-loc (zipper-domino-chain (create-domino-node [1 2] [[3 2] [4 5] [3 4] [2 7]])))


  ;; all chains can be built in one tree navigation. In the example below, 2 chains are built
  ;; and none is valid. Use z/next to deep-first navigate the tree until reach back the root (end? = true)
  (-> root-loc
      (z/append-child (create-domino-node [2 3] [[4 5] [3 4] [2 7]]))
      (z/append-child (create-domino-node [2 7] [[4 5] [3 4] [3 2]]))
      z/next   ;; move to [2 3]
      (z/append-child (create-domino-node [3 4] [[4 5] [2 7]]))
      z/next
      (z/append-child (create-domino-node [4 5] [[2 7]]))
      z/next  ;; [4 5] : no domino can be placed
      (z/edit #(assoc % :last true)) ;; end of this chain
      z/next  ;; nil 
      z/next  ;; [2 7] no domino can be placed
      (z/edit #(assoc % :last true)) ;; end of this chain
      z/next
      z/next
      ;;z/end?
      z/root
      ;;
      )
;;
  )


  ;; ...so we should be able to cretae the complete tree in one shot.

(defn terminate-chain [loc]
  (-> loc
      (z/edit #(assoc % :last true))))

(defn place-dominoes [loc]
  (let [{:keys [domino remain]} (z/node loc)]
    (z/next (if (empty? remain)
              (terminate-chain loc)
              (let [possible-connections (find-possible-connections domino remain)]
                (if (empty? possible-connections)
                  (terminate-chain loc)
                  (loop [connections possible-connections
                         loc loc]
                    (if (empty? connections)
                      (terminate-chain loc)
                      (let [[connected-domino remaining-dominoes] (first connections)]
                        (recur (rest connections)
                               (-> loc
                                   (z/append-child (create-domino-node connected-domino remaining-dominoes)))))))))))))

(defn valid-chain-terminator [loc head-domino-val]
  (let [{:keys [domino last remain]} (z/node loc)]
    (and last
         (empty? remain)
         (= (second domino) head-domino-val))))

(comment
  (valid-chain-terminator {:domino [3 4] :last true} 4)
  (valid-chain-terminator {:domino [3 5] :last true} 4)
  (valid-chain-terminator {:domino [4 5] :last true} 4)
  (valid-chain-terminator {:domino [6 5] :last true} 4))




(comment
  ;;(def root-loc (zipper-domino-chain (create-domino-node [1 2] [[2 3] [3 1] [2 4] [2 4]])))
  ;;(def root-loc (zipper-domino-chain (create-domino-node [1 2] [[2 3] [3 1] [1 1] [2 2] [3 3]])))
  ;;(def root-loc (zipper-domino-chain (create-domino-node [1 2] [[5 3] [3 1] [1 2] [2 4] [1 6] [2 3] [3 4] [5 6]])))
  (def root-loc (zipper-domino-chain (create-domino-node [1 2] [[2 3] [3 1] [4 4]])))


  (z/root (last (take-while #(not (z/end? %))
                            (iterate place-dominoes root-loc))))

  ;;(def placed-root-loc (zipper-domino-chain (z/root (last (take-while #(not (z/end? %))
  ;;                                                                    (iterate place-dominoes root-loc))))))
  (def placed-root-loc (->> (iterate place-dominoes root-loc)
                            (take-while #(not (z/end? %)))
                            last
                            z/root
                            zipper-domino-chain))

  (z/root placed-root-loc)
  (z/end? (last (take-while #(or (not (z/end? %))

                                 (let [node (z/node %)]
                                   (and (:last node)
                                        (= 1 (last (:domino node)))))) (iterate z/next placed-root-loc))))


  (->> (iterate z/next placed-root-loc)
       (drop-while #(and (not (z/end? %))
                         (not (valid-chain-terminator % 1))))
       first
       z/end?
       ;;z/node
       ;;z/path
       ;;(map :domino)
       ;;z/node
       )

;;
  )

(defn create-all-chains [root-loc]
  (->> (iterate place-dominoes root-loc)
       (take-while #(not (z/end? %)))
       last
       z/root
       zipper-domino-chain))

(defn can-chain? [])


