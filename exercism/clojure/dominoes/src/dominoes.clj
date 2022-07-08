(ns dominoes
  (:require [clojure.zip :as z]))

(defn flip [[v1 v2]] [v2 v1])

(defn can-connect-without-flip [[_ a2] [b1 _]] (= a2 b1))

(defn connect
  "Given 2 dominoes, where the first one is 'fixed' (i.e can't flip) and the second one is 
     a candidate for connection, returns nil if they can't connect or the candidate dominoe
     possibly fillped to connect to the first one."
  [fixed-domino candidate-domino]
  (if (can-connect-without-flip fixed-domino candidate-domino)
    candidate-domino
    (let [flipped-candidate-domino (flip candidate-domino)]
      (when (can-connect-without-flip fixed-domino flipped-candidate-domino)
        flipped-candidate-domino))))

(comment
  
(connect [1 2] [3 4])
(connect [1 2] [2 4])
(connect [1 2] [4 2])

  )
(defn remove-by-index
  "Given an int *index* and a seq of indexed items where each item is `[idx value]`
   returns the seq without item at index *idx* and with each item replaced by its value.

   Example:
   ```
   (remive-by-index 2 [[1 item1] [2 item2] [3 item3]])
   => [item1 item3]
   ```

   "
  [idx indexed-items]
  (->> (remove (fn [[current-idx _]]
                 (= current-idx idx))
               indexed-items)
       (map last)))

(defn find-possible-connections
  "Given a fixed domino (can't flip) and a list of dominoes candidates to connect,
   Returns a vector of all possible connections, where the first item is the (possibly flipped)
   domino that connects, and the second item is the list of remaining dominoes.
   Returns an empty vector when no connection is possible. "
  [fixed-domino dominoes]
  (let [indexed-dominoes (map-indexed vector dominoes)]
    (reduce (fn [result [idx domino]]
              (if-let [connected-domino (connect fixed-domino domino)]
                (conj result [connected-domino (remove-by-index idx indexed-dominoes)])
                result))
            []
            indexed-dominoes)))

(def zipper-domino-chain (partial z/zipper
                                  (constantly true)
                                  :children
                                  #(assoc %1 :children %2)))

(defn create-domino-node 
  "Create and returns a map representing a node in the domino chains tree"
  [domino remain]
  {:domino   domino    ;; the domino placed in the chain
   :remain   remain    ;; dominoes still not placed 
   :children []})      ;; list (possibly empty) of domino nodes connected

(defn terminate-chain 
  "Given a *loc*ation in the domino chain tree, mark it as end of chain meaning
   that none of remaining dominoes can connect, or no more remaining dominoe."
  [loc]
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

(defn valid-chain-terminator 
  "Returns true if the domino at the given location in the chain tree, is terminating the chain
   in a valid way."
  [loc head-domino-val]
  (let [{:keys [domino last remain]} (z/node loc)]
    (and last                                     ;; it is marked and chain terminator
         (empty? remain)                          ;; no unplaced dominoe remain
         (= (second domino) head-domino-val)      ;; first value of the chain is the same as last value (loop)
         )))

(defn valid-chain?
  "Given the root location and the not-connected value of the first domino 
   returns true if the chain tree contains a valid dominoes chain"
  [root-loc head-domino-val]
  (->> (iterate z/next root-loc)
       (drop-while #(and (not (z/end? %))
                         (not (valid-chain-terminator % head-domino-val))))
       first
       z/end?   ;; the end is reached : no chain have been found
       not))

(defn create-all-chains 
  "Given an initial location (the tree root), create and returns the root location of a new tree 
   where all possible dominoes chains have been added."
  [root-loc]
  (->> (iterate place-dominoes root-loc)
       (take-while #(not (z/end? %)))
       last
       z/root
       zipper-domino-chain))

(defn can-chain? [[first-domino & remaining-dominoes]]
  (let [root-loc (zipper-domino-chain (create-domino-node first-domino remaining-dominoes))]
    (valid-chain? (create-all-chains root-loc) (first first-domino))))


