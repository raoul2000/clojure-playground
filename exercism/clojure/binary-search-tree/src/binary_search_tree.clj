(ns binary-search-tree)

;; let's try to reprosent a node as a map with 3 keys
;; - :value the node value
;; - :left  the left subtree
;; - :right  the right subtree


(defn value [node]
  (:value node))

(defn singleton [value]
  {:value value})

(defn left [node]
  (:left node))

(defn right [node]
  (:right node))

(defn select-subtree [val node]
  (cond
    (> val (value node))  [:right (right node)]
    :else                 [:left  (left node)]))

(defn insert [value node]
  (let [[sel-key sel-branch] (select-subtree value node)]
    (assoc node sel-key (cond
                          (nil? sel-branch) (singleton value)
                          :else             (recur value sel-branch)))))

(defn from-list [xs]
  (reduce (fn [res val]
            (insert val res))
          (singleton (first xs))
          (rest xs)))


(defn to-list [node]
  (when node
    (-> (vector (to-list (:left node))
                (:value node)
                (to-list (:right node)))
        (remove nil?)
        flatten
        (into []))))

(comment
  (from-list [5 3  1 2 3])
  (to-list (from-list [2 1 3]))
  (to-list (singleton 2))
  ;;
  )



