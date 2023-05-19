(ns sfsdb.export
  (:require [sfsdb.options :as opts]
            [sfsdb.convert :as convert]
            [sfsdb.check :as check]
            [babashka.fs :as fs]))


;; maybe this function is not needed !!

(defn export
  "copy object *db-path* to the folder on the local file system at *fs-dir-path*."
  [db-path fs-dir-path {:keys [root-path]
                        :or {root-path (:root-path opts/default)}}]
  {:pre [db-path fs-dir-path root-path]}
  (check/validate-db-path db-path)
  (check/validate-root-path root-path)

  (when-not (and (fs/exists? fs-dir-path)
                 (fs/directory? fs-dir-path)
                 (fs/writable? fs-dir-path))
    (throw (ex-info "invalid destination folder path"
                    {:path fs-dir-path})))

  (let [fs-path (convert/db-path->fs-path db-path root-path)]
      (cond
        (fs/directory? fs-path)      (fs/copy-tree fs-path fs-dir-path)
        (fs/regular-file? fs-path)   (fs/copy      fs-path fs-dir-path))))

(comment
  (def base-path (fs/path (fs/cwd) "test/fixture/fs/root"))
  (export "folder-1" "c:\\tmp" {:root-path base-path})
  (export "folder-2/invalid-meta-1.txt" "c:\\tmp" {:root-path base-path})

  ;;
  )