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
  []
  (println "begin")
  (Thread/sleep (rand-int 3000))
  (println "end"))

(defn run-task-in-thread [t]
  (async/thread
    (for [x (range 1 4)]
      (do
        (println "starting")
        (t)
        (println "sleeping")
        (println "loop")))))




