(ns sfsdb.health.core)

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


(comment
  ;; another refacto



  (examine in-1 exams-2)

;;
  )