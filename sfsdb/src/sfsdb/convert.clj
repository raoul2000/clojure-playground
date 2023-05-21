(ns sfsdb.convert
  (:require [babashka.fs :as fs]
            [clojure.string :as s]
            [sfsdb.check :as check]))

(defn db-path->fs-path
  "Converts *db-path* to *fs-path* the corresponding file system path given *root-path*"
  [db-path root-path]
  (fs/path root-path db-path))

(defn fs-path->db-path
  "Converts *fs-path* an OS file system absolute path into a db path. The db *root-path*
   is an absolute path to the DB root folder.
   
   Example:
   ```clojure
   (fs-path->db-path \"c:\\folder1\\db\" \"c:\\folder1\\db\\folder2\\folder3\")
   => \"folder2/folder3\"
   ```
   "
  [root-path fs-path]
  {:pre [root-path fs-path (fs/absolute? root-path) (fs/absolute? fs-path)]
   :post [(check/in-db? %)]}
  (->> (fs/relativize root-path fs-path)
       fs/components
       (s/join "/")))


(defn absolutize-db-path [db-path parent-db-path]
  (when-not (check/relative-db-path? db-path)
    (throw (ex-info "not a relative db path"
                    {:db-path db-path})))
  (->> (fs/normalize (str parent-db-path "/" db-path))
       (fs/components)
       (s/join "/")))


