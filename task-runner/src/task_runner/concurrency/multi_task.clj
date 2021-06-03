(ns task-runner.concurrency.multi-task)

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
  "remove a task from the tyask list given its id"
  [id]
  (when (@tasks id)
    (swap! tasks #(dissoc % id))))

(comment
  (add-task (create-task #(println "foo")) "foo")
  (add-task (create-task #(println "bar")) "bar")

  (remove-task "bar")
  
  )

(defn list-task-id []
  (keys @tasks))

(comment
  (list-task-id))