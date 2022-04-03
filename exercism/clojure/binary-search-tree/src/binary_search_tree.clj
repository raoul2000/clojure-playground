(ns binary-search-tree)

;; let's try to represent a node as a 3 items arrays
;; - first item : left subtree or nil
;; - second item : the value
;; - third item : the right substree or nil

(defn singleton [value] [nil value nil])
(def value second)
(def left  first )
(def right last  )

(defn select-subtree
  [val node]
  (if (> val (value node))
    [2 (right node)]     ;; returns right selector key and subtree
    [0 (left node)]))    ;; returns left selector key and subtree

(defn insert [value node]
  (let [[sel-key sel-branch] (select-subtree value node)]
    (assoc node sel-key (cond
                          (nil? sel-branch) (singleton value)
                          :else             (insert value sel-branch)))))

(defn from-list [xs]
  (reduce (fn [res val] (insert val res))
          (singleton (first xs))
          (rest xs)))

(defn to-list [node]
  (->> (flatten node)
       (remove nil?)))

(comment
  (from-list [5 3  1 2 3])
  (to-list (from-list [2 1 3]))
  (to-list (singleton 2))
  ;;
  )



