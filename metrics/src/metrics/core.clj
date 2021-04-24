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
  "merge the entry defined by the pair vector into map m.
   The pair vector is [:colname  value] where value is a string
   If :colname is found in the conversion map, value is converted"
  [m pair conv]
  (let [k (first pair)
        v (second pair)
        convf (get conv k)]
    (assoc m k ((or convf identity) v))))

(defn mapify-line
  "transform a CSV line into a map given a conversion map describing columns"
  [line conv]
  (reduce #(add-entry %1 %2 conv)
          {}
          (map vector (keys conv) (str/split line #","))))

(defn parse
  [file]
  (map #(mapify-line %1 conversions) (str/split (slurp file) #"\n")))

(defn distrib-download-count
  [rec-seq]
  (let [download-count (map #(assoc {}
                                    :task-id (:task-id %)
                                    :latestDownloadCount (:latestDownloadCount %)) rec-seq)]
    (group-by :task-id download-count)))

(defn freq-download-count
  [map-distrib]
  (reduce-kv #(assoc %1 %2 (frequencies (map :latestDownloadCount %3)))
             {}
             map-distrib))

(defn print-dwn-count
  [rec]
  (doseq [i (seq rec)]
    (printf "task : %s\n" (first i))
    (let [total-download (reduce + (map second (second i)))]
      (printf "total-download = %d\n" total-download)
      (doseq [cnt (sort-by second (seq (second i)))]
        (printf "%02d = %02d (%02.2f %%)\n"
                (first cnt)
                (second cnt)
                (float (/ (* 100 (second cnt)) total-download)))))))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (if (zero? (count args))
    (println "no arg")
    (do
      (println args)
      (->> (apply concat (map parse args))
           distrib-download-count
           freq-download-count
           print-dwn-count))))

(defn main_1
  "I don't do a whole lot ... yet."
  [& args]
  (if (seq args)
    (doseq [arg args]
      (println arg)
      (print-dwn-count (->> (parse arg)
                            distrib-download-count
                            freq-download-count)))
    (println "no arg")))


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

  (parse "./test/fixtures/metrics-1.csv")

  (count (apply concat (map parse '("./test/fixtures/metrics-1.csv" "./test/fixtures/metrics-2.csv"))))

  (def filelist ["C:/dev/ws/lab/clojure-playground/metrics/test/fixtures/metrics-1.csv"
                 "C:/dev/ws/lab/clojure-playground/metrics/test/fixtures/metrics-2.csv"])
  (map parse filelist)
  (-main "C:/dev/ws/lab/clojure-playground/metrics/test/fixtures/metrics-1.csv"
         "C:/dev/ws/lab/clojure-playground/metrics/test/fixtures/metrics-2.csv")
  
  (-main 
         "C:/dev/ws/lab/clojure-playground/metrics/test/fixtures/metrics-2.csv")
  
  (count (parse "./test/fixtures/metrics-1.csv"))
  (count (parse "./test/fixtures/metrics-2.csv"))
  (time (distrib-download-count (parse "./test/fixtures/metrics-1.csv")))
  (freq-download-count (distrib-download-count (parse "./test/fixtures/metrics-2.csv")))

  (->> (parse "./test/fixtures/metrics-2.csv")
       distrib-download-count
       freq-download-count)

  (def m1 {"news" [{:task-id "news", :latestDownloadCount 0} {:task-id "news", :latestDownloadCount 0}]
           "photos" [{:task-id "photos", :latestDownloadCount 2} {:task-id "photos", :latestDownloadCount 3}]})

  (reduce-kv #(assoc %1 %2 (frequencies (map :latestDownloadCount %3))) {} m1)

  (def l1 [{:task-id "news", :latestDownloadCount 0} {:task-id "news", :latestDownloadCount 0}])

  (map :latestDownloadCount l1)

  (def l2 {"news" {0 39, 2 14, 1 22, 3 2}, "photos" {0 4, 7 3, 1 11, 4 1, 6 2, 3 10, 2 13, 9 3, 5 6, 10 8}})

  (doseq [i (seq l2)]
    (printf "task : %s\n" (first i))
    (doseq [cnt (sort (seq (second i)))]
      (printf "%02d = %02d\n" (first cnt) (second cnt))))

  (doseq [i (seq l2)]
    (printf "task : %s\n" (first i))
    (let [total-download (reduce + (map second (second i)))]
      (printf "total-download = %d\n" total-download)

      (doseq [cnt (sort-by second (seq (second i)))]
        (printf "%02d = %02d (%02.2f %%)\n"
                (first cnt)
                (second cnt)
                (float (/ (* 100 (second cnt)) total-download)))))))