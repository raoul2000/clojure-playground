(ns sfsdb.health.core
  (:require [sfsdb.check :refer [meta-file?]]
            [babashka.fs :as fs]
            [sfsdb.options :as opts]))

;; --------------------------

(defn meta-file-for-data-file?
  "Returns *true* if *path* refers to a metadata file linked to a fs file (i.e not a dir).
   
   Note that the related data file is not garanteed to exists.
   "
  [path]
  (and (meta-file? path)
       (not= (fs/file-name path) (str "." (:metadata-extension opts/default)))))

  ;; - :help : a string describing the exam
  ;; - :selected? : predicate function. argument is a Path. When it returns TRUE, the exam is applied to the item
  ;; - :examine  : exam function that take one argument the Path to test and returns a result

(def exams-2 {:metadata-orphan      {:help         "list all metadata files not related to a data file"
                                     :selected?    #(and (meta-file-for-data-file? %)
                                                         (fs/regular-file? %))
                                     :examine      #(-> % fs/strip-ext fs/exists?)
                                     :accumulator  (fn [result-coll new-result]
                                                     (cond-> result-coll
                                                       (not (:result new-result)) (conj  new-result)))}

              :empty-data-file       {:help         "Find all empty data files"
                                      :selected?    #(and (fs/regular-file? %)
                                                          (not (meta-file? %)))
                                      :examine      #(zero? (fs/size %))
                                      :accumulator  (fn [result-coll new-result]
                                                      (cond-> result-coll
                                                        (:result new-result) (conj  new-result)))}

              :data-file-total-size {:help          "Calculate total Data file size"
                                     :selected?     #(and (fs/regular-file? %)
                                                          (not (meta-file? %)))
                                     :examine       fs/size
                                     :accumulator   (fn [total-size file-size]
                                                      ((fnil + 0)  total-size (:result file-size)))}

              :list-dir-content     {:help          "Count files or dirs in all directories"
                                     :selected?     fs/directory?
                                     :examine       #(count (fs/list-dir %))}
              ;;
              })



;; --------------------------
(defn run-single-exam [subject]
  (fn [exam-report [exam-id {:keys [selected? examine accumulator]
                             :or {accumulator (fnil conj [])}}]]
    (if (selected? subject)
      (update exam-report exam-id (fn [old-exam-res]
                                    (accumulator old-exam-res {:subject subject
                                                               :result  (examine subject)})))
      exam-report)))

(defn apply-exams [exams]
  (fn [acc fs-path]
    (reduce (run-single-exam fs-path) acc (seq exams))))

(defn examine
  "Apply a map of exams to each item in *coll* and returns a exam result map
     
     - **key** : the exam id
     - **value** : a coll of exams results represented as maps with 2 keys
       - `:subject` : the item that was examined
       - `:result` : the result of exam as returned by the `:examine` function
     "
  [coll exams]
  (reduce (apply-exams exams) {} coll))

(comment
  ;; another refacto

  (def root (fs/path (fs/cwd) "test/fixture/fs/root2"))
  (fs/glob root "**")
  (examine (fs/glob root "**") exams-2)

  (map meta-file-for-data-file? (fs/glob root "**"))


  (map meta-file-for-data-file? (fs/glob (fs/path (fs/cwd) "test/fixture/fs/root3") "**"))

  (examine (fs/glob (fs/path (fs/cwd) "test/fixture/fs") "**") exams-2)

;;
  )