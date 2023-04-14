(ns sfsdb.read2
  (:require [babashka.fs :as fs]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [portal.console :as log]))

(def metadata-extension "extension for metadata file" "meta")
(def dot-meta-ext (str "." metadata-extension))

(defn- meta-file?
  "Given a file relatvie/absolute *path* returns TRUE if it refers to a metadata
   file, FALSE otherwise.
   
   *path* must be coercible to String"
  [path]
  (when-let [str-path (str path)]
    (str/ends-with? str-path dot-meta-ext)))

(defn in-db? [db-path]
  (not (->> db-path
            (fs/normalize)
            (fs/components)
            (map str)
            (some #(= ".." %)))))

(comment
  

  (last (fs/list-dir "test/fixture/fs/root/folder-1"))
  (meta-file? "a\\d\\.meta")
  (meta-file? (first  (fs/list-dir "test/fixture/fs/root/folder-1")))
  (fs/path "a/b/.meta")
  (meta-file? (fs/path "a/b/.meta"))
  (meta-file? (fs/path ".meta"))
  (meta-file? "a/b/.meta")
  (meta-file? ".meta")

  (fs/extension "a\\b\\.meta"))

(defn- make-metadata-path
  "Given a *path* returns the path to the metadata file describing *path*.
   The returned path is not garanteed to exsits on the file system."
  [path]
  (if (fs/directory? path)
    (fs/path path (str "." metadata-extension))
    (fs/path (str path "." metadata-extension))))

(defn- read-meta
  "Returns the metadata map describing *path* which can be a file or a folder.
   
   When *path* doesn't exist or when no metadata exists for this *path*, returns `nil`.
   When the meta value is not valid JSON, a string describing the error is returned "
  [path]
  (let [meta-path (make-metadata-path path)]
    (when (and (fs/exists?       meta-path)
               (fs/regular-file? meta-path))
      (try
        (json/read-str (slurp (fs/file meta-path)) :key-fn keyword)
        (catch Exception e (str "caught exception: " (.getMessage e)))))))

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
  ;; build a nested map describing a tree folder structure
  ;;

  (def root-fs1 {"folder1"        {:meta         {:prop1      "value1"
                                                  :prop2      12}

                                   "file1.json"  {:meta       {:prop1 "prop value 1"}
                                                  :content    "{\"prop\": 12 }"}

                                   "sub-folder1" {:meta       {:prop1 "value2"}
                                                  "file1.txt" {:meta {:type "f"}
                                                               :content "file content"}
                                                  "file2.txt" {:content "content for file 2"}}}

                 "file0.xml"      {:meta         {:file-info "some value"}
                                   :content      "<root>value</root>"}
                 "empty-folder"   {}
                 "empty-file.txt" {:content      ""}})

  ;; reading meta
  ;; read folder or file meta
  (get-in root-fs1 ["folder1" :meta])
  (get-in root-fs1 ["folder1" "sub-folder1" :meta])
  (get-in root-fs1 ["folder1" "sub-folder1" "file1.txt" :meta])
  ;; file or folder has no meta
  (get-in root-fs1 ["folder1" "sub-folder1" "file2.txt" :meta])
  (get-in root-fs1 ["file0.xml" :meta])
  (get-in root-fs1 ["empty-folder" :meta])

  #_(defn read-meta [root-fs path-v]
      (get-in root-fs (conj path-v :meta)))

  #_(read-meta root-fs1 ["folder1" "sub-folder1" "file1.txt"])
  #_(read-meta root-fs1 ["folder1" "sub-folder1" "file2.txt"])

  ;; read  file content
  (defn read-file-content [root-fs path-v]
    (get-in root-fs (conj path-v :content)))

  (get-in root-fs1 ["file0.xml" :content])
  (read-file-content root-fs1 ["file0.xml"])
  (read-file-content root-fs1 ["folder1" "sub-folder1" "file2.txt"])

  ;; ls folder
  (defn ls [root-fs folder-v]
    (remove (partial = :meta) (keys (get-in root-fs folder-v))))
  (ls root-fs1 ["folder1"])
  (ls root-fs1 ["folder1" "sub-folder1"])

  ;; file-exists?
  (defn file-exists? [root-fs path-v]
    (contains? (get-in root-fs (conj path-v)) :content))

  (get-in root-fs1 (conj ["folder1" "file1.json"] :content))
  (file-exists? root-fs1 ["folder1" "file1.json"])
  (file-exists? root-fs1 ["folder1" "not_found"])

  ;; dir-exists?



  (def result (volatile! []))

  (defn explore-folder [folder-path]
    {:name folder-path
     :content (fs/walk-file-tree folder-path
                                 {:pre-visit-dir (fn [path _attr]
                                                   (println "pre : %s" path)
                                                   (vswap! result conj (str path))

                                                   :continue
                                                   ;;:skip-subtree
                                                   )
                                  :post-visit-dir (fn [path _attr]
                                                    (println "post : %s" path)
                                                    :continue)})})

  (explore-folder (fs/path (fs/cwd) "test/fixture/fs/root"))


  (fs/walk-file-tree (fs/path (fs/cwd) "test/fixture/fs/root")
                     {:pre-visit-dir (fn [path _attr] :skip-subtree)})

  ;;
  )