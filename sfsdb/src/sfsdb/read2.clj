(ns sfsdb.read2
  (:require [babashka.fs :as fs]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [portal.console :as log]))

(def metadata-extension "extension for metadata file" "meta")

(defn meta-file? [path]
  (= metadata-extension (fs/extension path)))

(defn make-metadata-path
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

(defn read [db-path {:keys [with-meta? root-path]
                     :or   {root-path (fs/cwd)}}]
  (let [path (fs/path root-path db-path)]
    (when (fs/exists? path)
      (if (fs/directory? path)
        (read-directory path root-path with-meta?)
        (read-file      path root-path with-meta?)))))

(comment
  (def root-path (fs/path (fs/cwd)))

  (read "test/fixture/fs/root/folder-1" {:with-meta? true})
  (read "test/fixture/fs/root/folder-1/folder-1-A" {:with-meta? true})
  ;;
  )