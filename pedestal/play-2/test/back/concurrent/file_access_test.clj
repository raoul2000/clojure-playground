(ns concurrent.file-access-test
  (:require [babashka.fs :as fs]
            [clojure.core.async :as async]))

;; see https://stackoverflow.com/questions/6404717/idiomatic-file-locking-in-clojure

(def work-dir "c:\\tmp\\concurrent")

(defn write-to-file [n]
  (let [content   (apply str (conj (repeat 50 (str n)) \newline))
        file-path (str (fs/path work-dir "file-1.txt"))]
    (println (format "%s" content))
    (Thread/sleep 1000)
    (spit file-path
          content
          :append true)
    (flush)))

(defn print-to-stdout [n]
  (prn n))

(def writer-chan (async/chan 100))



(comment

  (async/go 
    (println (async/<! writer-chan) ))
  (async/>!! writer-chan "hey")
  
  (spit (str (fs/path work-dir "file1.txt")) "some text")
  (apply str (repeat 5 "e"))

  (write-to-file 10)


  (async/go
    (prn "goo"))
  (dotimes [n 9]
    (async/go
      (write-to-file n)
      ;;(print-to-stdout n)
     ))

  ;;
  )
