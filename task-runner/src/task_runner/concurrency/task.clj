(ns task-runner.task)

(defn sleep-random
  [id]
  (let [sleep (rand-int 3000)]
    (println (str "begin " id " " sleep))
    (Thread/sleep (rand-int sleep))
    (println (str "end " id " " sleep))
    sleep))

(defn sleep-fixed
  [id sleep-ms]
  (println (str " ---- begin " id " " sleep-ms))
  (Thread/sleep (rand-int sleep-ms))
  (println (str " ---- end " id " " sleep-ms))
  sleep-ms)