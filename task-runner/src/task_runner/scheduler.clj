(ns task-runner.scheduler)

(defn rnd-sleep []
  (->> 4
       rand-int
       inc
       (* 1000)
       Thread/sleep))

(defn simple-task-1 [id]
  (println "-" id ": starting ")
  (rnd-sleep)
  (println "-" id ": stop"))

(defn create-task [f]
  (Thread. f))

(defn generate-tasks [cnt]
  (for [i (range 1 (inc cnt))]
    (partial simple-task-1 i)))

(defn sched [task]
  (doto (create-task task) .start))

(defn test-run1 []
  (for [task (generate-tasks 5)]
    (doto (create-task task) .start)))

(comment
  (* (Math/random) 2000)
  (* (+ (rand-int 4) 1) 1000)
  (->> 4
       rand-int
       inc
       (* 1000))
  (for [id [1 2 3]]
    (partial simple-task-1 id)))



