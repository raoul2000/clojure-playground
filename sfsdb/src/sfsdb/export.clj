(ns sfsdb.export
  (:require [sfsdb.options :as opts]
            [sfsdb.convert :as convert]
            [sfsdb.check :as check]
            [babashka.fs :as fs]))


(defn- copy-folder [src dest]

  (fs/create-dir (fs/path dest (fs/file-name src)))
  (doseq [src (->> (fs/glob src "**" {:recursive true})
                   (remove check/meta-file?))]
    (if (fs/directory? src)
      ()
      ())))

;; maybe this function is not needed !!

(defn export
  "copy object *db-path* to the folder on the local file system at *fs-dir-path*."
  ;; TODO: the dest folder must not be refering to the db itself
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

  (def src-dir-fs-path (fs/path (fs/cwd) "test/fixture/fs/root/folder-1"))
  (def dest-dir-fs-path (fs/path (fs/cwd) "test/fixture/export"))

  (fs/glob src-dir-fs-path "**" {:recursive true})


  (fs/glob src-dir-fs-path "**/.meta" {:recursive true})
  (fs/glob src-dir-fs-path "**/file-1B-1.txt" {:recursive true})

  (fs/file-name "/a/b/c")
  (fs/create-dir "")
  (doseq [src (->> (fs/glob src-dir-fs-path "**" {:recursive true})
                   (remove check/meta-file?))]
    (if (fs/directory? src)
      ()
      ()))


  (fs/file-name "/e/r/.meta")


  (fs/file-name "/e/r/.meta")

  ;;
  )