(ns toolbox.depend.file-tree
  (:require  [babashka.fs :as fs]))


(defn read-fs-tree [root-path depth]
  (let [file-list (volatile! [])]
    (fs/walk-file-tree root-path {:visit-file (fn [path attr]
                                                (when-not (.isDirectory attr)
                                                  (vswap! file-list #(conj % path)))
                                                :continue)
                                  :max-depth depth})
    @file-list))

(defn list-all-files [root-path]
  (->> (read-fs-tree root-path nil)
       (map (memfn toString))
       (map #(.replace % "\\" "/"))))

(comment
  (read-fs-tree "./test/fixture" 4)
  (list-all-files "./test/fixture/root-1"))

