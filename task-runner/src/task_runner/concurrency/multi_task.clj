(ns task-runner.concurrency.multi-task)

;; { taskId-1 {:func f1}
;;   taskId-1 {:func f2}}
(def tasks-atom (atom {}))

(defn create-task [f]
  {:func f
   :running false
   :interrupt false})

(defn add-task
  "add task t with id in the task list. Returns the new entry
   or nil if id already exists"
  [t id]
  (when-not (@tasks-atom id)
    (swap! tasks-atom #(assoc % id t))))

(defn remove-task
  "remove a task from the task list given its id"
  [id]
  (when (@tasks-atom id)
    (swap! tasks-atom #(dissoc % id))))

(comment
  (add-task (create-task #(println "foo")) "foo")
  (add-task (create-task #(println "bar")) "bar")

  (remove-task "bar")
  (remove-task "foo"))

(defn list-task-id
  "returns a list of all task id in the task list"
  []
  (keys @tasks-atom))

(comment
  (list-task-id)
  ;;
  )

;; repeately execute fn t until timeout or
;; interrupt. Pause n ms between successive call to t
(defn run-task [id tsk]
  (println (str "run-task : " id))
  (swap! tsk #(assoc % :running true :interrupt false))
  (let [res (deref (future (tsk :func)) 2000 :timeout)]
    (cond
      (= res :timeout)           (println "?????? timeout")
      (deref (tsk :interrupt))   (println "====== interrupt")
      :else (do
              (println (str "result = " res ". waiting ..and start again"))
              (Thread/sleep 100)
              (recur id tsk)))))

(defn interrupt-task [])

(defn start-scheduler []
  (for [tks (seq tasks-atom)
        :let [[id task] tks]
        :when (not (task :running))]
    (run-task id task))
  (Thread/sleep 1000)
  (recur))

(defn stop-scheduler []
  (for [tks (seq tasks-atom)
        :let [[id task]] tks]
    (swap! tasks-atom )))

(defn task-A
  [id]
  (let [sleep (rand-int 3000)]
    (println (str "begin " id " " sleep))
    (Thread/sleep (rand-int sleep))
    (println (str "end " id " " sleep))))

(defn start-0 []
  (loop [[cur & remaining] (seq @tasks-atom)]
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
  (set-run-task "foo" @tasks-atom false))

(defn start []
  (loop [[cur & remaining] (seq @tasks-atom)]
    (when-let [[id task] cur]
      (println (str "task-id: " id))
      (if (not (realized? (task :future)))
        "running"
        (let [fut-task (future ((task :func)))]
          (swap! tasks-atom (partial set-future-task id fut-task))))
      (recur remaining)))
  (println "done"))

(defn loop-tasks []
  (for [x [1 2 3]]
    (do
      (start)
     ;;(Thread/sleep 10)
      )))


(comment
  (add-task (create-task #(task-A "foo")) "foo")
  (add-task (create-task #(task-A "bar")) "bar")
  (start)
  (loop-tasks)
  ;;
  )
