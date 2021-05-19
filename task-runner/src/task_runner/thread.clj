(ns task-runner.thread
  (:require [clojure.string :as s]))


(def my-thread (Thread. (fn []
                          (Thread/sleep 1000)
                          (print "hi"))))

(def t (future (Thread/sleep 1000)
               (print "hello")
               100))

(defn gogo []
  (.start my-thread)
  (println "A"))

(defn fake-fetch []
  (Thread/sleep
   (Thread/sleep 5000)
   "Ready!"))
