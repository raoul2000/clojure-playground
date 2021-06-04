(ns task-runner.concurrency.multi-task)

;; { taskId-1 {:func f1}
;;   taskId-1 {:func f2}}
(def tasks (atom {}))

(defn create-task [f]
  {:func f})

(defn add-task
  "add task t with id in the task list. Returns the new entry
   or nil if id already exists"
  [t id]
  (when-not (@tasks id)
    (swap! tasks #(assoc % id t))))

(defn remove-task
  "remove a task from the task list given its id"
  [id]
  (when (@tasks id)
    (swap! tasks #(dissoc % id))))

(comment
  (add-task (create-task #(println "foo")) "foo")
  (add-task (create-task #(println "bar")) "bar")

  (remove-task "bar")
  (remove-task "foo"))

(defn list-task-id
  "returns a list of all task id in the task list"
  []
  (keys @tasks))

(comment
  (list-task-id)
  ;;
  )

(defn task-A
  [id]
  (let [sleep (rand-int 3000)]
    (println (str "begin " id " " sleep))
    (Thread/sleep (rand-int sleep))
    (println (str "end " id " " sleep))))

(defn start-0 []
  (loop [[cur & remaining] (seq @tasks)]
    (when-let [[id task] cur]
      (println (str "task-id: " id))
      (future ((task :func)))
      (recur remaining)))
  (Thread/sleep 2000)
  (println "done"))

(defn set-run-task [task-id v task-map]
  (update task-map task-id #(assoc % :running v)))

(defn set-future-task [task-id fut task-map]
  (update task-map task-id #(assoc % :future fut)))

(comment
  (set-run-task "foo" @tasks false))

(defn start []
  (loop [[cur & remaining] (seq @tasks)]
    (when-let [[id task] cur]
      (println (str "task-id: " id))
      (if (not (realized? (task :future)))
        "running"
        (let [fut-task (future ((task :func)))]
          (swap! tasks (partial set-future-task id fut-task))))
      (recur remaining)))
  (println "done"))

(defn loop-tasks []
  (for[x [1 2 3]]
   (do
     (start)
     ;;(Thread/sleep 10)
     ))
  )


(comment
  (add-task (create-task #(task-A "foo")) "foo")
  (add-task (create-task #(task-A "bar")) "bar")
  (start)
  (loop-tasks)
  ;;
  )
