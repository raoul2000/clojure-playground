(ns dev.fstree
  (:require [babashka.fs :as fs]
            [clojure.string :as s]))

;; create a memory representation (map) of the file system

;; the idea is to recursively read the entire content if a given folder 
;; and produce a map describing is. This map, with the apropriate structure and keys
;; could then be converted into an XML document or used as is.

;; to select in a XML document, XPath are used
;; to select in a map, https://github.com/redplanetlabs/specter could be used

(comment

  ;; how about using babashka/fs to access the file system ?
  (def root (fs/path (fs/cwd) "test/fixture/fs/root"))

  (filter fs/directory? (fs/list-dir "c:\\tmp"))
  (fs/list-dir root "*")

  ;; recursively browse the folder
  (into {} (loop [[dir & rest-dirs] [root]
                  result {}]
             (if-not dir
               result
               (recur (into rest-dirs (filter fs/directory? (fs/list-dir dir)))
                      (assoc result dir (fs/list-dir dir))))))
  ;; good but the created map is not deep enough. 
  ;; it look like this:
  (def bad-result1 {"folder1" ["file1" "subfolder1" "file2"]
                    "subfolder1" ["file3" "subfolder2"]})
  ;; when in fact we want something like
  (def bad-result1 {"folder1" ["file1"
                               {"subfolder1" ["file3"
                                              {"subfolder2" []}]}
                               "file2"]})

  ;; we could use the path as a map selector to update nested values
  ;; Tokenize the path and use each folder name as key array
  (assoc-in {} ["folder1" "folder2"] "file1.txt")
  ;; => {"folder1" {"folder2" "file1.txt"}}

  ;; let's explore this option. 
  ;; To work correctly we must relativize each subfolder of the root folder and this
  ;; can be achieve by the fs/relativize function
  (fs/relativize "/a/b/c" "/a/b/c/d/e")
  ;; => "d/e"
  (fs/relativize "/a/b/c" "/a/b/c")
  ;; => ""

  ;; let's imagine we can get this map
  (def m1 {"folder1" {:dir? true
                      :children {"file1.txt" {:dir? false}
                                 "file2.txt" {:dir? false}
                                 "subfolder1" {:dir? true
                                               :children {"file3.txt" {:dir? false}}}
                                 "file4.txt" {:dir? false}
                                 "subfolder2" {:dir? true
                                               :children {"file5.txt" {:dir? false}
                                                          "subfolder3" {:dir? true
                                                                        :children {"file6.txt" {:dir? false}
                                                                                   "file7.txt"  {:dir? false}}}}}}}})

  (get-in m1 ["folder1"])
  (get-in m1 ["folder1" :children "subfolder2" :children "subfolder3"])
  ;; this is nice, but the use of the !children key could be avoided
  (def m2 {"folder1" {:attr "folder1 attributs"
                      "file1.txt" {:attr "file1.txt attributes"}
                      "file2.txt" {:attr "file2.txt attributes"}
                      "subfolder1" {:attr "subfolder1 attributes"
                                    "file3.txt"  {:attr "file2.txt attributes"}
                                    "subfolder2" {:attr "subfolder2 attributes"
                                                  "file4.txt" {:attr "file4.txt attributes"}}}}})
  ;; get folder1 info
  (get-in m2 ["folder1"])
  (get-in m2 ["folder1" "subfolder1"])
  (get-in m2 ["folder1" "subfolder1" "subfolder2"])

  ;; let's add a new subfolder to subfolder2
  (update-in m2 ["folder1" "subfolder1" "subfolder2"] assoc "file5.txt" {:attr "file5.txt attributes"})

  ;; let's add a new folder tree
  (update-in m2 ["folder1" "subfolderA" "subfolderB"] assoc "file6.txt" {:attr "file6.txt attributes"})
  ;; works ok but "subfolderA" and "subfolderB" are added with no :attr map !
  ;; It should not be a problem. 
  ;; let' verify it work ok when using deep tree walk

  ;; first we need a function to update the map, given a relative folder path

  (defn path->ks [path]
    (into [] (map str (fs/components path))))

  (defn update-dir [m dir-path k v]
    (update-in m  (path->ks dir-path) assoc k v))

  (defn create-dir [m dir-path v]
    (assoc-in m (path->ks dir-path) v))


  ;; create dir tree ...
  (-> {"folder1" {}}
      (create-dir "folder2" {})
      (update-dir "folder2" "file1.txt" {})
      (create-dir "folder2/folder3" {})
      (update-dir "folder2/folder3" "file2.txt" {})
      (update-dir "folder2/folder3" "file3.txt" {}))

  ;; create dir tree with no order
  (-> {}
      (create-dir "folder2/folder3/folder4" {})
      (create-dir "folder2/folder3" {}))

  ;; this fails : the second overwirte the first: dir tree should be browsed starting from the top and going down
  ;; and this is the way fs/walk-file-tree works

  (let [result (volatile! {})]
    (fs/walk-file-tree root {:pre-visit-dir (fn [item _attr]
                                              (vswap! result create-dir (str (fs/relativize root item)) {:dir? true})
                                              :continue)
                             :visit-file (fn [item _attr]
                                           (vswap! result update-dir
                                                   (str (fs/relativize root (fs/parent item)))
                                                   (fs/file-name item)
                                                   {:dir? false})
                                           :continue)})
    @result)

  ;; successful result : a tree map structure describine the dir tree
  (def the-result {"" {:dir? true},
                   "folder-1"
                   {:dir? true,
                    ".meta" {:dir? false},
                    "folder-1-A"
                    {:dir? true,
                     ".gitkeep" {:dir? false},
                     ".meta" {:dir? false},
                     "file-1A-1.txt" {:dir? false},
                     "file-1A-1.txt.meta" {:dir? false},
                     "file-1A-2.txt" {:dir? false},
                     "folder-1-A-blue" {:dir? true, ".gitkeep" {:dir? false}}},
                    "folder-1-B"
                    {:dir? true,
                     ".gitkeep" {:dir? false},
                     "file-1B-1.txt" {:dir? false},
                     "file-1B-2.txt" {:dir? false},
                     "file-1B-3.txt" {:dir? false}}},
                   "folder-2"
                   {:dir? true, ".gitkeep" {:dir? false}, "invalid-meta-1.txt" {:dir? false}, "invalid-meta-1.txt.meta" {:dir? false}}})

  ;; this map can be browsed
  ;; all items in "folder-1/:folder-1-A" :
  (get-in the-result ["folder-1" "folder-1-A"])

  ;; get only files in "folder-1/:folder-1-A" :
  (->> (get-in the-result ["folder-1" "folder-1-A"])
       (filter (fn [[_file-name {:keys [dir?]}]]
                 (not dir?)))
       (into {}))



  ;;
  )
