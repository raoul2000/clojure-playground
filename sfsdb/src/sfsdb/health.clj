(ns sfsdb.health
  (:require [babashka.fs :as fs]
            [sfsdb.options :as opts]
            [sfsdb.check :as check]
            [sfsdb.convert :as convert]))

;; check DB health state

;; rules
;; level 1 : ERRORS
;; - no orphean metadata file : a metadata file not related to an object
;; - invalid JSON metadata file : metadata file with invalid JSON content
;; - db contains symlinks: only dir and regular files are allowed
;; level 2 : WARNINGS
;; - file with empty content
;; - dir with no content 
;; - deep nested: configurable max depth check


(defn file-pair 
  "Returns a map :
   - `:meta-file`: *meta-file-path*
   - `:data-file`: the data file assumed to be linked to *meta-file-path*

   Pairing between data and metadata file is based on naming conventions

   "
  [meta-file-path]
  (hash-map :meta-file  meta-file-path
            :data-file  (->> meta-file-path
                             fs/file-name
                             fs/strip-ext
                             (fs/path (fs/parent meta-file-path)))))

(defn data-file-exist 
  "Add key `:data-file-exists?` and corresponding value to map."
  [{:keys [data-file]
                        :as file-pair}]
  (assoc file-pair :data-file-exist? (fs/regular-file? data-file)))


(defn find-metadata-orphan
  "Given *dir-fs-path* a folder path, returns a list of all
   metadata files not linked to any regular file.
   
   Returns a list of maps :
   - `:data-file`: path of the missing data file
   - `:meta-file`: path of the orphan metadata file
   "

  [db-path {:keys [root-path]
            :or   {root-path (:root-path opts/default)}}]
  {:pre [db-path]}
  (check/validate-root-path root-path)
  (check/validate-db-path   db-path)
  (let [dir-fs-path (convert/db-path->fs-path db-path root-path)
        glob-pattern (str "**/?*." (:metadata-extension opts/default))]

    (when-not (fs/directory? dir-fs-path)
      (throw (ex-info "folder not found"
                      {:db-path db-path
                       :fs-path dir-fs-path})))
    
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


