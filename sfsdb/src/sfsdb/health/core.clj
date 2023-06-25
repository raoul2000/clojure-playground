(ns sfsdb.health.core
  (:require [sfsdb.check :refer [meta-file?]]
            [babashka.fs :as fs]
            [sfsdb.options :as opts]))



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
                       :selected?  (constantly true)
                       :examine    (constantly (rand-int 10))}})


;; --------------------------

(defn meta-file-for-data-file?
  "Returns *true* if *path* refers to a metadata file linked to a fs file."
  [path]
  (and (meta-file? path)
       (not= (fs/file-name path) (str "." (:metadata-extension opts/default)))))


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


(comment
  ;; another refacto



  (examine in-1 exams-2)

;;
  )