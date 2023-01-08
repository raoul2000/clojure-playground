(ns sfsdb.read
  (:require [babashka.fs :as fs]
            [clojure.string :as s]
            [clojure.data.json :as json]))

(def re-path-splitter (re-pattern  (str "\\" fs/file-separator)))

;; on windows, fs/component skips the drive letter
(defn- split-paths
  "Splits string *path-str* representing a path using local filesystem settings,
   into an array of folder or file names."
  [^String path-str]
  (s/split path-str re-path-splitter))

(defn- normalize-path [^String path]
  (let [norm-path (str (fs/normalize path))]
    (if (s/includes? norm-path "\\")
      (s/replace norm-path #"\\" "/")
      norm-path)))

(comment
  (normalize-path "ee/rr")
  (normalize-path "./ee/rr")
  (normalize-path "./ee/rr.txt")
  (normalize-path "ee\\rr")
  (normalize-path ".\\ee\\rr")

  (fs/components "c:\\aa\\bb")
  (fs/normalize "./ee/RR")
  (fs/normalize "~/ee/RR")
  (clojure.string/includes? "eee\\ee" "\\")
  (.firstIndexOf "eee" "e"))


(defn- ensure-absolute-path [path]
  (if (fs/relative? path) (fs/absolutize path) path))

(defn- path-seq
  "Given *root-path* a folder path, returns a seq of strings representing all
   folder path relative to *root-path*. The path separator is always '/'.
   
   example:
   ```
   (path-seq \"/a/b/c\")
   => (\"d1\"
       \"d1/e\"
       \"d1/e/f\"
       \"d1/e/f\"
       \"d2\")
   ```
   If *root-path* is not an absolute path, it is assumed to be relative to
   the current working dir.
   "
  [root-path]
  (let [absolute-root-path (fs/normalize (ensure-absolute-path root-path))
        relativize-to-root (partial fs/relativize absolute-root-path)]
    (->> (fs/glob absolute-root-path "**")
         (filter fs/directory?)
         (map (comp normalize-path
                    relativize-to-root)))))


(defn make-folder-metadata-path [folder-path]
  (fs/path folder-path ".meta"))

(defn folder-info [folder-path]
  (let [meta-folder-path (make-folder-metadata-path folder-path)]
    (when (fs/exists? meta-folder-path)
      (json/read-str (slurp (fs/file meta-folder-path)) :key-fn keyword))))

(comment
  (def base-path (fs/path (fs/cwd) "test/fixture/fs/root/folder-1"))
  (def base-path (fs/path (fs/cwd) "test/fixture/fs/root/folder-2"))
  (def base-path  "test/fixture/fs/root/folder-2")
  (folder-info base-path)
  ;;
  )