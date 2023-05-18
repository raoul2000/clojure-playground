(ns sfsdb.read
  (:require [babashka.fs :as fs]
            [clojure.string :as s]
            [clojure.data.json :as json]
            [sfsdb.options :as opts]
            [sfsdb.check :as check]
            [sfsdb.convert :as convert]))

(defn- make-metadata-path
  "Returns the fs path of a metadata file describing file or folder at *fs-path*.
   
   The returned path is not garanteed to exsit on the file system."
  [fs-path]
  (if (fs/directory? fs-path)
    (fs/path fs-path (str "." (:metadata-extension opts/default)))
    (fs/path (str fs-path "." (:metadata-extension opts/default)))))

(defn- read-meta
  "Returns the metadata map describing a file or folder at *fs-path* or nil if none is found.
   
   Throws if the metadata file is not a valid JSON format.   
   "
  [fs-path]
  (let [meta-path (make-metadata-path fs-path)]
    (when (and (fs/exists?       meta-path)
               (fs/regular-file? meta-path))
      (try
        (json/read-str (slurp (fs/file meta-path)) :key-fn keyword)
        (catch Exception ex
          (throw (ex-info "failed to read metadata file"
                          {:path (str meta-path)
                           :cause  (.getMessage ^Throwable ex)}
                          ex)))))))

(defn- fs-path->obj
  "Read and returns a map describing the DB object given its absolute FS path *fs-path*. When
   *with-meta?* is true, key `:meta` is set to contain the metadata of the object if found."
  [fs-path root-path with-meta?]
  {:pre [(fs/exists? fs-path)]}
  (cond-> {:name (fs/file-name  fs-path)
           :dir? (fs/directory? fs-path)
           :path (convert/fs-path->db-path root-path fs-path)}
    with-meta? (assoc :meta (read-meta fs-path))))

(defn- read-directory [dir-path root-path with-meta? with-content?]
  (cond-> (fs-path->obj dir-path root-path with-meta?)
    with-content?  (assoc :content (->> (fs/list-dir dir-path)
                                        (remove check/meta-file?)
                                        (map #(fs-path->obj % root-path with-meta?))))))

(defn- read-file [file-path root-path with-meta? with-content?]
  (when-not (check/meta-file? file-path)
    (cond-> (fs-path->obj file-path root-path with-meta?)
      with-content? (assoc :content (slurp (str file-path))))))


(comment
  (defn f1 [p]
    {:pre [(fs/exists? p)
           (fs/directory? p)
           (fs/readable? p)]}
    "ok")

  (try
    (f1  "eee" #_(fs/cwd))
    (catch Exception e {:msg (ex-message e)
                        :data (ex-data e)}))

  (def root-path (fs/path (fs/cwd)))

  (read-db-path "test/fixture/fs/root/folder-1" {:with-meta? true})
  (read-db-path "c:\\tmp" {:with-meta? true})
  (read-db-path "test/fixture/fs/root/folder-1/folder-1-A" {:with-meta? true})
  (read-db-path "test/fixture/fs/root/folder-1/folder-1-A/file-1A-1.txt" {:with-meta? true})
  ;;
  )



(defn read-db-path
  "Returns a map describing the file or a folder at `db-path` or nil if it doesn't exist.
   
   Option maps:
   - `:with-content?` : read object content
   - `:with-meta?` : read object metadata
   - `:root-path` : base folder base used to resolve `db-path`. If not set, *current 
   working dir* is used
   
   Throws if `db-path` is absolute path or not in DB.
   "
  [db-path {:keys [with-meta? with-content? root-path]
            :or   {root-path (:root-path opts/default)}}]
  {:pre [db-path]}
  (check/validate-root-path root-path)
  (check/validate-db-path   db-path)
  (let [fs-path (convert/db-path->fs-path db-path root-path)]
    (when (fs/exists? fs-path)
      (if (fs/directory? fs-path)
        (read-directory fs-path root-path with-meta? with-content?)
        (read-file      fs-path root-path with-meta? with-content?)))))


(defn list-all-dirs
  "Given folder at `root-path`, returns a seq of maps, each one describing a descendant folder of
   `root-path` with metadata when `with-meta?` is true.
   
   If `root-path` is not absolute, it is assumed to be relative to *current working dir*.
   
   Return *nil* when `root-path` doesn't exists or is not a directory.
   "
  [root-path with-meta?]
  {:pre [root-path]}
  (check/validate-root-path root-path)
  (let [path-coll (volatile! [])
        abs-path (fs/absolutize root-path)]
    (when (and (fs/exists? abs-path)
               (fs/directory? abs-path))
      (fs/walk-file-tree abs-path {:pre-visit-dir (fn [path _attr]
                                                    (when-not (= path abs-path)
                                                      (vswap! path-coll conj path))
                                                    :continue)})
      (map #(fs-path->obj % abs-path with-meta?) @path-coll))))







