(ns sfsdb.read
  (:require [babashka.fs :as fs]
            [clojure.string :as s]
            [clojure.data.json :as json]
            [portal.console :as log]))


(def metadata-extension "extension for metadata file" ".meta")
(def re-path-splitter (re-pattern  (str "\\" fs/file-separator)))

;; note that on windows, fs/components skips the drive letter
;; and we don't want that
(defn- split-paths
  "Splits string *path-str* representing a path using local filesystem settings,
   into an array of folder or file names."
  [^String path-str]
  (s/split path-str re-path-splitter))

(comment
  (fs/components "c:\\folder\\subfolder\\file.txt")
  (split-paths "c:\\folder\\subfolder\\file.txt")

  (fs/unixify (fs/file "c:\\folder\\subfolder\\file.txt"))

  ;;
  )


(defn- normalize-path [^String path]
  (when path
    (let [norm-path (str (fs/normalize path))]
      (if (s/includes? norm-path "\\")
        (s/replace norm-path #"\\" "/")
        norm-path))))

(comment
  (normalize-path nil)
  (normalize-path "")
  ;;
  )

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

(defn make-metadata-path
  "Given a *path* returns the path to the metadata file describing *path*.
   The returned path is not garanteed to exsits on the file system."
  [path]
  (if (fs/directory? path)
    (fs/path path metadata-extension)
    (fs/path (str path metadata-extension))))

(defn- read-meta
  "Returns the metadata map describing *path* which can be a file or a folder.
   When *path* doesn't exist or when no metadata exists for this *path*, returns `nil`."
  [path]
  (let [meta-path (make-metadata-path path)]
    (when (and (fs/exists?       meta-path)
               (fs/regular-file? meta-path))
      (json/read-str (slurp (fs/file meta-path)) :key-fn keyword))))

(defn read-folder-content
  "Returns a seq of normalized path (file or folder) contained by *folder-path*. Metadata
   files are ignored.
   
   Relativization is based on *root-path*.
   "
  [folder-path root-path]
  (->> (fs/list-dir folder-path)
       (remove #(s/ends-with? % metadata-extension))
       (map  (comp normalize-path (partial fs/relativize root-path)))))

(defn read-folder [folder-path {:keys [with-meta? root-path]
                                :or   {root-path (fs/cwd)}}]
  (when (and (fs/exists?    folder-path)
             (fs/directory? folder-path))
    (cond-> {:folder? true
             :content (read-folder-content folder-path root-path)} 
      with-meta? (assoc ,,,, :meta (read-meta folder-path))
            
            )

    #_(vector (read-folder-content folder-path root-path)
            (when with-meta?
              (read-meta folder-path))))
  
  )
(comment
  (cond->  1
    true (inc))
  ;;
  )

(comment

  (def root-path (fs/path (fs/cwd)))
  (tap> root-path)

  (def base-path (fs/path (fs/cwd) "test/fixture/fs/root/folder-1"))
  (def base-path (fs/path (fs/cwd) "test/fixture/fs/root/folder-2"))
  (def base-path  "test/fixture/fs/root/folder-2")

  (fs/relativize root-path (fs/path (fs/cwd) "test/fixture/fs/root/folder-1"))

  (read-folder (fs/path root-path "/test/fixture/fs/root/folder-2/")
               {:with-meta? false
                :root-path  root-path})
  (read-folder (fs/path root-path "/test/fixture/fs/root/folder-1")
               {:with-meta? true})

  (fs/walk-file-tree (fs/path (fs/cwd) "test/fixture/fs/root")
                     {:pre-visit-dir  (fn [path attr] (println path) :continue)
                      :post-visit-dir (fn [path attr] (println path) :continue)
                      :visit-file     (fn [path attr] (println path) :skip-siblings)})

  ;;
  )


(defn read-file [file-path {:keys [with-meta?]}]
  (when (and (fs/exists?       file-path)
             (fs/regular-file? file-path))
    (vector (slurp (fs/file file-path))
            (when with-meta?
              (read-meta file-path)))))

(comment
  (read-file (fs/path (fs/cwd)  "test/fixture/fs/root/folder-1/folder-1-A/file-1A-1.txt")
             {:with-meta? true})

  (fs/exists? (fs/path (fs/cwd)  "test/fixture/fs/root/folder-1/folder-1-A/file-1A-1.txt"))
  ;;
  )

