(ns sfsdb.select
  (:require [babashka.fs :as fs]
            [clojure.string :as s]
            [sfsdb.options :as opts]
            [sfsdb.check :as check]
            [sfsdb.convert :as convert]
            [sfsdb.read :as read]))

(defn- parent-of
  "Returns the parent db path of *db-path* or nil if *db-path* has no parent (i.e is root).
   
   Example:
   ```clojure
   (parent-of \"a/b/c\")
   => \"a/b\"
   (parent-of \"a\")
   => nil
   ``` 
   "
  [^String db-path]
  (when-let [parent (butlast (s/split db-path #"/"))]
    (s/join  "/" parent)))


(defn select-ancestors
  "Returns all objects ancestors of *db-path* where *selected? object* is true.
   Ancestors are ordered from closest to farthest relatively to *db-path*.
   
   *options* is the same map as in `read-db-path` with possibly extra key:
   - `:find-first?` : when true, returns only first ancestor
   "
  [db-path selected? {:keys [find-first? root-path]
                      :or   {root-path (:root-path opts/default)}
                      :as   options}]
  {:pre [(fn? selected?)]}
  (check/validate-root-path root-path)
  (check/validate-db-path   db-path)
  (loop [parent (parent-of db-path)
         result []]
    (if (or (nil? parent)
            (and find-first?
                 (not-empty result)))
      result
      (recur (parent-of parent)
             (let [parent-obj (read/read-db-path parent options)]
               (cond-> result
                 (selected? parent-obj) (conj parent-obj)))))))


(defn- walk-and-select [dir-path selected? {:keys [root-path]
                                            :as   options}]
  (let [result    (volatile! [])
        fn-filter (fn [fs-path]
                    (let [db-path (convert/fs-path->db-path root-path fs-path)
                          obj     (read/read-db-path  db-path options)]
                      (when (selected? obj)
                        (vswap! result conj obj))))]
    (fs/walk-file-tree dir-path {:pre-visit-dir (fn [fs-path _attr]
                                                  (when-not (= fs-path dir-path)
                                                    (fn-filter fs-path))
                                                  :continue)
                                 :visit-file    (fn [fs-path _attr]
                                                  (when-not (check/meta-file? fs-path)
                                                    (fn-filter fs-path))
                                                  :continue)})
    @result))

(defn select-descendants
  "Selects all objects descendant of *db-path* where *(selected? object)* returns true.
   
   - *db-path* must refer to an existing dir.
   - *options* is the same map as in `read-db-path`.
   "
  [db-path selected? {:keys [root-path]
                      :or   {root-path (:root-path opts/default)}
                      :as   options}]
  {:pre [(fn? selected?)]}
  (check/validate-root-path root-path)
  (check/validate-db-path   db-path)
  (let [path (fs/path root-path db-path)]
    (when (fs/directory? path)
      (walk-and-select path selected? options))))