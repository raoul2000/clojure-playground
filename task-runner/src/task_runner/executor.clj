(ns task-runner.executor
  (:import [java.util.concurrent ExecutorService Executors TimeUnit]))


(defn build-executor []
  (. Executors newSingleThreadScheduledExecutor))

(defn go []
  (let [exec-srv (build-executor)]
    (.schedule exec-srv
               (fn [] (println "hello")) 2 (. TimeUnit SECONDS))))

;; -----------------------------------------------------------------

(defn f1 []
  (println (rand-int 100))
  (flush))

(defn go2 []
  (let [exec-srv (build-executor)]
    (map #(.schedule exec-srv % 2 (. TimeUnit SECONDS))
         (repeat 3 f1))))

;; -- wip 

(defn go3 []
  (let [exec-srv (build-executor)]
    (map #(.schedule exec-srv % 1 (. TimeUnit SECONDS))
         (repeat 3 f1))
    (println "all tasks scheduled")
    (.awaitTermination exec-srv 5 (. TimeUnit SECONDS))
    (println "executor service stopped")))




