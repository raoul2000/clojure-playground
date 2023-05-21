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


(defn absolutize-db-path 
  "Given *db-path* a relative Db path, returns the absolute Db path
   relatively to *parent-db-path*.
   
   If *db-path* if not relative it is returned unchanged.

   Note that the returned Db path is not garanteed to be `in-db?`.
   "
  [^String db-path ^String parent-db-path]
  {:pre [(string? db-path) (string? parent-db-path)]}
  (if (check/relative-db-path? db-path)
    (->> (fs/normalize (str parent-db-path "/" db-path))
         (fs/components)
         (s/join "/"))
    db-path))


