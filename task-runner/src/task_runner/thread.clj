(ns task-runner.thread
  (:require [clojure.string :as s]
           [clojure.core.async :as async] ))


(def my-thread (Thread. (fn []
                          (Thread/sleep 1000)
                          (println "123456789 ... 10 !")
                          (flush))))

(def t (future (Thread/sleep 3000)
               (print "hello")
               100))

(defn gogo []
  (.start my-thread)
  (println "A"))

(defn t2 []
  (.start (Thread. (fn []
                     (println "start")
                     (Thread/sleep 1000)
                     (println "end")))))
(defn fake-fetch []
  (
   (Thread/sleep 5000)
   "Ready!"))

(defn user-async []
  (async/thread (Thread/sleep 1000)
                (println "bye bye")
                nil)
  (println "returning"))
(comment
  (gogo)
  (doto (Thread. (fn []
                   (Thread/sleep 1000)
                   (println "done")
                   (flush)))
    .start)
  
  )
