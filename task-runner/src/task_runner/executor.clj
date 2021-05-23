(ns task-runner.executor
  (:import [java.util.concurrent ExecutorService Executors TimeUnit]))


(defn build-executor []
  ;;(Executors/newFixedThreadPool 3)
  (. Executors newFixedThreadPool 3)
  ;;(. Executors newSingleThreadScheduledExecutor)
  )

(defn go []
  (let [exec-srv (build-executor)]
    (.schedule exec-srv
               (fn [] (println "hello")) 2 (. TimeUnit SECONDS))))

;; -----------------------------------------------------------------

(defn f1 []
  (let [rnd-sleep (* 500 (inc (rand-int 5)))
        thread-id (.getName (Thread/currentThread))]
    (println (format ">> [%s] %s" rnd-sleep thread-id))
    (Thread/sleep rnd-sleep)
    (println (format "<< [%s]" rnd-sleep))
    (flush)))

(defn go2 []
  (let [pool (Executors/newFixedThreadPool 3)]
    (map #(.execute pool %)
         (repeat 10 f1))))

;; -- wip 

(defn go3 []
  (println "main:" (.getName (Thread/currentThread)))
  (let [pool (Executors/newFixedThreadPool 3)]
    (map #(.execute pool %)
         (repeat 5 f1))
    ;;(println "all tasks scheduled")
    ;;(.awaitTermination pool 5 (. TimeUnit SECONDS))
    ;;(println "executor service stopped")
    ))

;; ----------------------------------------------------

(def waiting-msg "Waiting on threads to finish...")
(defn func-1 [msg]
  (let [sleep-time (+ 1000 (rand-int 4000))]
    (println "task " msg " - sleep " sleep-time)
    (Thread/sleep sleep-time)
    (println (.getName (Thread/currentThread)) "finishing")
    (Thread/sleep 20)
    #{:sleep sleep-time :msg msg}))

(def done-msg    "All threads joined. Exiting.")

;; Java ExecutorService, invokeAll

(defn go4 []
  (let [tp (Executors/newCachedThreadPool)
        threads (repeat 5 (partial func-1 "1"))]
    (println waiting-msg)
    (.invokeAll tp threads)
    (println done-msg)))

(defn go5 []
  (let [tp (Executors/newCachedThreadPool)]
    (println waiting-msg)
    (.submit tp (partial func-1 "go-5"))
    (.shutdown tp)
    (.awaitTermination tp 4  TimeUnit/SECONDS)
    (println done-msg)))

(defn go6 []
  (let [func  (partial func-1 "go-6")
        stp  (Executors/newScheduledThreadPool 3)]
    (println waiting-msg)
    (.scheduleWithFixedDelay stp func 0 1 TimeUnit/SECONDS)
    (Thread/sleep 2)
    (.shutdown stp)
    (.awaitTermination stp 4 TimeUnit/SECONDS)
    (println done-msg)))

(defn go7 []
  (let [all-func (repeat 5 (partial func-1 "go-7"))
        stp (Executors/newScheduledThreadPool 3)
        fut (map #(.scheduleWithFixedDelay stp % 0 250 TimeUnit/MILLISECONDS)
                 all-func)]
    (println waiting-msg)
    (println (count fut))
    (Thread/sleep 5000)
    (.shutdown stp)
    (.awaitTermination stp 4  TimeUnit/SECONDS)))

;; interactive ---------------------------------------------------
;; from repl run (sched #(println "A"))
;; to stop run stop-stp
(def stp  (Executors/newScheduledThreadPool 3))

(defn sched [t]
  (.scheduleWithFixedDelay stp t 0 250 TimeUnit/MILLISECONDS))

(defn stop-stp []
  (.shutdown stp)
  (println "shutting down ...")
  (.awaitTermination stp 2 TimeUnit/SECONDS)
  (println "shutdown!"))


;; ---------------------------------
;; wip
(def task-1-run (atom false))
(defn toggle-task-1-run []
  (swap! task-1-run not))

(defn task-1)

(defn task-1 []
  (for [round (range)
        :while @task-1-run]
    (Thread/sleep 1000)
    (println round)))

