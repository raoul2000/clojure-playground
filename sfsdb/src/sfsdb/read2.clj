(ns sfsdb.read2
  (:require [babashka.fs :as fs]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [portal.console :as log]))

(def metadata-extension "extension for metadata file" "meta")

(defn- meta-file? [path]
  (when path
    (= metadata-extension (fs/extension path))))

(defn- make-metadata-path
  "Given a *path* returns the path to the metadata file describing *path*.
   The returned path is not garanteed to exsits on the file system."
  [path]
  (if (fs/directory? path)
    (fs/path path (str "." metadata-extension))
    (fs/path (str path "." metadata-extension))))

(defn- read-meta
  "Returns the metadata map describing *path* which can be a file or a folder.
   When *path* doesn't exist or when no metadata exists for this *path*, returns `nil`."
  [path]
  (let [meta-path (make-metadata-path path)]
    (when (and (fs/exists?       meta-path)
               (fs/regular-file? meta-path))
      (json/read-str (slurp (fs/file meta-path)) :key-fn keyword))))

(defn- path->db-path [root-path path]
  (->> (fs/relativize root-path path)
       fs/components
       (str/join "/")))

(comment
  (path->db-path (fs/path "c:\\folder1") (fs/path "c:\\folder1\\folder2"))
  (path->db-path (fs/path "c:\\folder1") (fs/path "c:\\folder1\\folder2\\folder3"))
  (path->db-path (fs/path (fs/cwd)) (fs/path (fs/cwd) "aaa"))
  (fs/components (fs/relativize (fs/path (fs/cwd)) (fs/path (fs/cwd) "aaa/bbb")))
  (fs/split-paths (str (fs/relativize (fs/path (fs/cwd)) (fs/path (fs/cwd) "aaa\\bbb"))))
  (fs/components (str (fs/relativize (fs/path (fs/cwd)) (fs/path (fs/cwd) "aaa\\bbb"))))

  (str/join "/" (fs/components (str (fs/relativize (fs/path (fs/cwd)) (fs/path (fs/cwd) "aaa\\bbb")))))


  ;;
  )

(defn- path->obj [path root-path with-meta?]
  (cond-> {:name (fs/file-name path)
           :dir? (fs/directory? path)
           :path (path->db-path root-path path)}
    with-meta? (assoc :meta (read-meta path))))

(defn- read-directory [dir-path root-path with-meta?]
  (-> (path->obj dir-path root-path with-meta?)
      (assoc :content (->> (fs/list-dir dir-path)
                           (remove meta-file?)
                           (map #(path->obj % root-path with-meta?))))))

(comment
  (def root-path (fs/cwd))
  (read-directory (fs/path (fs/cwd) "test/fixture/fs/root/folder-1")
                  root-path
                  true)
  (read-directory (fs/path (fs/cwd) "test/fixture/fs/root/folder-1/folder-1-A")
                  root-path
                  true)
  (read-directory (fs/path (fs/cwd) "test/fixture/fs/root/folder-2")
                  root-path
                  true)
  ;;
  )

(defn- read-file [file-path root-path with-meta?]
  (when-not (meta-file? file-path)
    (-> (path->obj file-path root-path with-meta?)
        (assoc :content (slurp (str file-path))))))

(comment
  (def root-path (fs/cwd))
  (read-file (fs/path (fs/cwd) "test/fixture/fs/root/folder-1/.meta")
             root-path
             true)
  (read-file (fs/path (fs/cwd) "test/fixture/fs/root/folder-1/folder-1-A/file-1A-1.txt")
             root-path
             true)
  ;;
  )

(defn read-db-path
  "Returns a map describing the file or a folder at `db-path`. 
   
   Option maps:
   - `:with-meta?` : read object metadata
   - `:root-path` : base folder base used to resolve `db-path`. If not set, *current 
   working dir* is used
   
   Throws is `db-path` is not relative.
   "
  [db-path {:keys [with-meta? root-path]
            :or   {root-path (fs/cwd)}}] 
  (let [path (fs/path root-path db-path)]
    (when (fs/exists? path)
      (if (fs/directory? path)
        (read-directory path root-path with-meta?)
        (read-file      path root-path with-meta?)))))

(comment
  (def root-path (fs/path (fs/cwd)))

  (read-db-path "test/fixture/fs/root/folder-1" {:with-meta? true})
  (read-db-path "c:\\tmp" {:with-meta? true})
  (read-db-path "test/fixture/fs/root/folder-1/folder-1-A" {:with-meta? true})
  (read-db-path "test/fixture/fs/root/folder-1/folder-1-A/file-1A-1.txt" {:with-meta? true})
  ;;
  )

(defn list-all-dirs
  "Given folder at `root-path`, returns a seq of maps, each one describing a descendant folder of
   `root-path` with metadata when `with-meta?` is true.
   
   If `root-path` is not absolute, it is assumed to be relative to *current working dir*.
   
   Return *nil* when `root-path`doesn't exists or is not a directory.
   "
  [root-path with-meta?]

  (let [path-coll (volatile! [])
        abs-path (fs/absolutize root-path)]
    (when (and (fs/exists? abs-path)
               (fs/directory? abs-path))
      (fs/walk-file-tree abs-path {:pre-visit-dir (fn [path _attr]
                                                    (when-not (= path abs-path)
                                                      (vswap! path-coll conj path))
                                                    :continue)})
      (map #(path->obj % abs-path with-meta?) @path-coll))))

(comment
  (def root-path (fs/path (fs/cwd) "test"))

  (list-all-dirs root-path true)
  (list-all-dirs "test" true)
  (list-all-dirs "NOT_FOUND" true)
  (list-all-dirs "test/fixture/fs/root/folder-1/folder-1-A/file-1A-1.txt" true)

  ;;
  )