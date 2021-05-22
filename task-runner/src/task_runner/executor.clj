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

(comment
  (go3)
  (go2)
  (f1))




