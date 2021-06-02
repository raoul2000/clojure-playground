(ns task-runner.concurrency.first
  (:require [clojure.core.async
             :as async
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]))

(defn task1
  "dummy task: print and sleep random"
  []
  (println "begin")
  (Thread/sleep (rand-int 3000))
  (println "end"))

;; run in parallel (pcalls) 2 tasks
(defn run-wait-run-stop []
  (println "start")
  (apply pcalls  (repeat 2 task1))
  (println "end"))


;; task communication with core.async
(def continue? (atom true))
(def start-chan (async/chan))
(def end-chan   (async/chan))

(defn task2
  [id]
  (println (str "begin " id))
  (Thread/sleep (rand-int 1000))
  (println (str "end " id))
  id)

;; run async task once, and block until it returns a result
(let [fut (future (task2 :b))]
  (println "waiting ...")
  (Thread/sleep 2000)
  (println (str "res = " @fut)))

;; task wait rand ms and terminates
(defn task3
  [id]
  (let [sleep (rand-int 3000)]
    (println (str "begin " id " " sleep))
    (Thread/sleep (rand-int sleep))
    (println (str "end " id " " sleep))
    sleep))


;; run t expecting a future. wait the future is realized or
;; some time (loop)
(defn run-task [t]
  (let [fut (future (t))]
    (loop [cnt 20]
      (println cnt)
      (if (or (= 0 cnt) (realized? fut))
        :done
        (do
          (Thread/sleep 50)
          (recur (dec cnt)))))))

(comment
  (run-task (partial task3 :t3)))

;; stop execution
(def interrupt (atom true))

;; repeately execute fn t until timeout or
;; interrupt. Pause n ms between successive call to t
(defn run-task2b [t]
  (let [res (deref (future (t)) 2000 :timeout)]
    (cond
      (= res :timeout) "timeout"
      @interrupt       "interrupt"
      :else (do
              (println (str "result = " res ". waiting ..and start again"))
              (Thread/sleep 100)
              (recur t)))))

(comment
  (deref (future (run-task2b (partial task3 :t1000))))
  
  )

