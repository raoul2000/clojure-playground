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


(defn- apply-exam-on [item]
  (fn [[exam-id {:keys [can-pass-exam? pass-exam?]}]]
    (when (and (can-pass-exam?   item)
               (not (pass-exam?  item)))
      (hash-map  exam-id item))))

(defn- diagnose-item [exams-map]
  (fn [item]
    (->> (map (apply-exam-on item) exams-map)
         (remove nil?))))

(defn- exams-results [acc  m]
  (reduce (fn [acc2 k]
            (let [exam-result (get m k)]
              (if (vector? (get acc2 k))
                (update acc2 k conj exam-result)
                (assoc acc2 k (vector exam-result)))))
          acc
          (keys m)))

(defn diagnose
  "Apply all exams in *exams-map* to Db objects starting from *db-path* which must be a dir.
   
   *exams-map* has following shape:
   - key: the exam identifier
   - value: a map describing the exam:
     - `:apply?` : predicate on file system object
     - `:fn"
  [db-path exams-map {:keys [root-path]
                      :or   {root-path (:root-path opts/default)}}]
  {:pre [db-path]}
  (check/validate-root-path root-path)
  (check/validate-db-path   db-path)
  (let [dir-fs-path  (safe-create-dir-path db-path root-path)]
    (->> (fs/glob dir-fs-path "**")
         (map (diagnose-item exams-map))
         (flatten)
         (reduce exams-results {}))))

(comment
  (def exams-1 {:metadata-orphan {:help "describe metadata orphan exam"
                                  :can-pass-exam? (constantly true)
                                  :pass-exam?     (constantly true)}
                :empty-data-file {:help "describe empty data file"
                                  :can-pass-exam? (constantly true)
                                  :pass-exam?     (constantly false)}
                :dummy           {:help "describe dummy test"
                                  :can-pass-exam? (constantly true)
                                  :pass-exam?     (constantly false)}})

  (diagnose "" exams-1  {:root-path (fs/path (fs/cwd) "test/fixture/fs/root3")})
  ;;
  )

(comment
  ;; another refacto

  ;; - :help : a string describing the exam
  ;; - :selected? : predicate function. argument is a Path. When it returns TRUE, the exam is applied to the item
  ;; - :examine  : exam function that take one argument the Path to test and returns a result

  (def exams-2 {:exam-1 {:help       "ex1"
                         :selected?  (constantly true)
                         :examine    (constantly {:result "ok"})}
                :exam-2 {:help       "ex2"
                         :selected?  (constantly true)
                         :examine    (constantly true)}
                :size   {:help       "data file size"
                         :selected? (constantly true)
                         :examine   (constantly (rand-int 10))}})

  (defn run-single-exam [subject]
    (fn [exam-report [exam-id {:keys [selected? examine]}]]
      (if (selected? subject)
        (update exam-report exam-id (fn [old-exam-res]
                                      ((fnil conj []) old-exam-res {:subject subject
                                                                    :result  (examine subject)})))
        exam-report)))

  (defn apply-exams [exams]
    (fn [acc fs-path]
      (reduce (run-single-exam fs-path) acc (seq exams))))

  (def in-1 ["item-1" "item-2" "item-3"])

  (defn examine 
    "Apply a map of exams to each item in *coll* and returns a exam result map
     
     - **key** : the exam id
     - **value** : a coll of exams results represented as maps with 2 keys
       - `:subject` : the item that was examined
       - `:result` : the result of exam as returned by the `:examine` function
     "
    [coll exams]
    (reduce (apply-exams exams) {} coll))
  

  (examine in-1 exams-2)



  ;;
  )

