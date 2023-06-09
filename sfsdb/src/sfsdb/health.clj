(ns sfsdb.health
  (:require [babashka.fs :as fs]
            [sfsdb.options :as opts]
            [sfsdb.check :as check]
            [sfsdb.convert :as convert]))

;; check DB health state

;; rules
;; level 1 : ERRORS
;; X no orphean metadata file : a metadata file not related to an object
;; - invalid JSON metadata file : metadata file with invalid JSON content
;; - db contains symlinks: only dir and regular files are allowed
;; level 2 : WARNINGS
;; X file with empty content
;; - dir with no content 
;; - deep nested: configurable max depth check (not sure)


(defn- file-pair
  "Returns a map :
   - `:meta-file`: *meta-file-path*
   - `:data-file`: the data file assumed to be linked to *meta-file-path*

   Pairing between data and metadata file is based on naming conventions"
  [meta-file-path]
  (hash-map :meta-file  meta-file-path
            :data-file  (->> meta-file-path
                             fs/file-name
                             fs/strip-ext
                             (fs/path (fs/parent meta-file-path)))))

(defn- data-file-exist
  "Add key `:data-file-exists?` and corresponding value to map."
  [{:keys [data-file]
    :as file-pair}]
  (assoc file-pair :data-file-exist? (fs/regular-file? data-file)))


(defn- safe-create-dir-path
  "Given a *db-dir-path* a Db path to an existing dir object, and *root-path*, the DB FS root folder path,
   return the FS folder path for the dir object. 
   
   throws if the fs path does not exit or is not a folder."
  [db-dir-path root-path]
  (let [dir-fs-path  (convert/db-path->fs-path db-dir-path root-path)]
    (when-not (fs/directory? dir-fs-path)
      (throw (ex-info (str "folder not found: " dir-fs-path)
                      {:db-dir-path db-dir-path
                       :fs-path     dir-fs-path})))
    dir-fs-path))

(defn find-metadata-orphan
  "Given *db-path* a folder Db path, returns a list of all
   metadata files not linked to any regular file contained in *db-path*.
   
   Returns a list of maps :
   - `:data-file`: path of the missing data file
   - `:meta-file`: path of the orphan metadata file
   "

  [db-path {:keys [root-path]
            :or   {root-path (:root-path opts/default)}}]
  {:pre [db-path]}
  (check/validate-root-path root-path)
  (check/validate-db-path   db-path)
  (let [dir-fs-path  (safe-create-dir-path db-path root-path)
        glob-pattern (str "**/?*." (:metadata-extension opts/default))]
    (->> (fs/glob dir-fs-path glob-pattern)
         (map file-pair)
         (map data-file-exist)
         (remove :data-file-exist?))))

(comment

  (find-metadata-orphan "" {:root-path (fs/path (fs/cwd) "test/fixture/fs/root3")})
  (find-metadata-orphan "a" {:root-path (fs/path (fs/cwd) "test/fixture/fs/root3")})
  (find-metadata-orphan "a/1" {:root-path (fs/path (fs/cwd) "test/fixture/fs/root3")})
  (find-metadata-orphan "" {:root-path (fs/path (fs/cwd) "test/fixture/fs/NOT_FOUND")})
  (find-metadata-orphan "" {:root-path (fs/path (fs/cwd) "test/fixture/fs/root3/alone.meta")})

  (find-metadata-orphan "1" {:root-path (fs/path (fs/cwd) "test/fixture/fs/root3")})
  (find-metadata-orphan "alone.meta" {:root-path (fs/path (fs/cwd) "test/fixture/fs/root3")})

  ;;
  )

(defn find-empty-file
  "Returns a list of all the path to empty file found in the Dir object *db-path*."
  [db-path {:keys [root-path]
            :or   {root-path (:root-path opts/default)}}]
  {:pre [db-path]}
  (check/validate-root-path root-path)
  (check/validate-db-path   db-path)
  (let [dir-fs-path  (safe-create-dir-path db-path root-path)
        glob-pattern "**"]

    (->> (fs/glob dir-fs-path glob-pattern)
         (remove #(or (fs/directory? %)
                      (= "meta" (fs/extension %))))
         (filter #(zero? (fs/size %)))
         (map str))))

(comment
  (find-empty-file "" {:root-path (fs/path (fs/cwd) "test/fixture/fs/root3")})
  (find-empty-file "a/alone.meta" {:root-path (fs/path (fs/cwd) "test/fixture/fs/root3")})
  (find-empty-file "not_found" {:root-path (fs/path (fs/cwd) "test/fixture/fs/root3")})
  (find-empty-file "" {:root-path (fs/path (fs/cwd) "test/fixture/fs/root")})
  ;;
  )



(def exams-catalog {:metadata-orphan {:help "find all metadata files linked with no data file"
                                      :fn identity}
                    :empty-data-file {:help "find all empty data files"
                                      :fn identity}})


(defn- apply-exam [exams-id-xs]
  (fn [path]
    (map (fn [exam-id]
           (when-let [exam (get exams-catalog exam-id)]
             (hash-map :exam-id exam-id
                       :result  ((:fn exam) path)))) exams-id-xs)))

(defn diagnose [db-path exam-seq {:keys [root-path]
                                  :or   {root-path (:root-path opts/default)}}]
  {:pre [db-path]}
  (check/validate-root-path root-path)
  (check/validate-db-path   db-path)
  (let [dir-fs-path  (safe-create-dir-path db-path root-path)]

    (map (apply-exam exam-seq) (fs/glob dir-fs-path "**"))))
