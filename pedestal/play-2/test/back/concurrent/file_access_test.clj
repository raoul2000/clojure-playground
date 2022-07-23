(ns concurrent.file-access-test
  (:require [babashka.fs :as fs]
            [clojure.core.async :as async]
            [clojure.string :refer [join]]))

;; see https://stackoverflow.com/questions/6404717/idiomatic-file-locking-in-clojure

(def work-dir "c:\\tmp\\concurrent")


;; first let's highlight issue when several threads are writting to the same file 

(defn write-to-file [n file-path]
  (let [content   (join "-" (conj (repeat 50 (str n)) \newline))]
    (println (format "%s" content))
    (Thread/sleep (rand-int 100))
    (spit file-path
          content
          :append true)
    (Thread/sleep (rand-int 100))
    (flush)
    file-path))

;; each go block is scheduled for execution in an undetermined order. In the
;; end, all lines are consistant but not written in order

(defn test-1 []
  (dotimes [n 50]
    (async/go
      (write-to-file n (str (fs/path work-dir "file-1.txt"))))))

;; all lines are written in order

(defn test-2 []
  (let [file-agent (agent (str (fs/path work-dir "file-1.txt")))
        watcher    (fn [k r os ns] (print "lll"))]
    (add-watch file-agent :write watcher)
    (dotimes [n 50]
      (send-off file-agent (partial write-to-file n)))
    (prn "await...")
    (await file-agent)
    (prn "done")))

  ;;(shutdown-agents)

