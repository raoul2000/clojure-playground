(ns crawler.core
  (:require [clj-http.client :as client])
  (:gen-class))

;; https://reqres.in/api/users

(comment
  (client/get "https://reqres.in/api/users" {:as :json})

  ((fn [s1 s2]
     (conj '() (first s2) (first s1))) '(1 2) '(:a :b))

  (for [i (range 3)
        l ()]
    i)
  
  ((fn [s1 s2]
     (loop [l1 s1
            l2 s2
            i1 (first l1)
            i2 (first l2)
            acc '()]
       (if (and (nil? i1) (nil? i2))
         acc
         (recur (rest l1) (rest l2)))))
   '(1 2 3) '(:a :b :c))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
