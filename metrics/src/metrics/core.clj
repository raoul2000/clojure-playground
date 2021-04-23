(ns metrics.core
  (:require [clojure.string :as str])
  (:gen-class))


(defn str->int
  [str]
  (Integer. (str/trim str)))

(def conversions {:date identity
                  :task-id identity
                  :latestDownloadCount str->int
                  :execCount str->int
                  :latestExecDurationMs str->int
                  :totalExecDurationMs str->int
                  :avgExecDurationMs str->int
                  :downloadTotalCount str->int})

(defn add-entry
  "adds the pair [k v] entry to the map m applying conv(ersion)
   if defined for k"
  [m pair conv]
  (let [k (first pair)
        v (second pair)
        convf (get conv k)]
    (assoc m k ((or convf identity) v))))

(defn mapify-line
  [line conv]
  (reduce #(add-entry %1 %2 conv) {}
          (map vector (keys conv) (str/split line #","))))

(defn parse
  [file]
  (map #(mapify-line %1 conversions) (str/split (slurp file) #"\n")))

(defn distrib-download-count
  [rec-seq]
  (let [download-count (map #(dissoc % 
                                     :execCount
                                     :date
                                     :latestExecDurationMs
                                     :totalExecDurationMs
                                     :avgExecDurationMs
                                     :downloadTotalCount) rec-seq)]
    ;(group-by :task-id (frequencies download-count)))
    (group-by :task-id  download-count))
  
  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (if (seq args)
    (doseq [arg args]
      (println arg)
      (parse arg))
    (println "no arg"))
  (println "Hello, World!"))


(comment
  (def s '({:task-id "T1"
            :latestDownloadCount 2}
           {:task-id "T2"
            :latestDownloadCount 6}
           {:task-id "T1"
            :latestDownloadCount 5}
           {:task-id "T2"
            :latestDownloadCount 6}))
  (filter #(= "T1" (:task-id %1)) s)

  (frequencies s)
  (reduce #(+ %1 (:latestDownloadCount %2)) 0
          (filter #(= "T1" (:task-id %1)) s))


  (time (distrib-download-count (parse "./test/fixtures/metrics-1.csv")))
  )