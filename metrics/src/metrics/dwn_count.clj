(ns metrics.dwn-count)

(defn select-download-count-col
  [map-seq]
  (map :latestDownloadCount map-seq))

(defn complete-serie
  "given a map where keys are int, returns a new map
   where all keys between 0 and (max key) are present with a value 
   set to 0 if not already present in m"
  [m]
  (let [max-key (apply max (keys m))
        zero-vals (reduce #(assoc %1 %2 0) {} (range 0 (inc max-key)))]
    (merge zero-vals m)))

(defn download-count-freq
  [map-seq]
  (->> map-seq
       select-download-count-col
       frequencies
       complete-serie
       (sort-by key)))

(defn dwn-count-by-task
  "given a seq of rows, returns a map  where the key is the task id
   and the value is a seq of [download-count freq]"
  [rec-seq]
  (->> rec-seq
       (group-by :task-id)
       (reduce #(assoc %1 (first %2) (download-count-freq (second %2))) {})))

(defn print-dwn-freq-by-task
  [freqs]
  (let [tot-run (reduce #(+ %1 (second %2)) 0 freqs)
        tot-dwn (reduce #(+ %1 (* (first %2) (second %2))) 0 freqs)]
    (println "dwn/run;tot. run;percent run")
    (doseq [f freqs]
      (printf "%d;%d;%02.2f\n" (first f) (second f) (float (/ (* 100 (second f)) tot-run))))
    (printf "total run = %d\n" tot-run)
    (printf "total download = %d\n\n" tot-dwn)))

(defn print-csv
  [freq-by-task]
  (doseq [entry freq-by-task]
    (printf "task : %s\n" (first entry))
    (print-dwn-freq-by-task (second entry))))

(defn report
  [rows]
  (->> rows
       dwn-count-by-task
       print-csv))

(comment
  (def map-seq1 [{:date "date-1" :task-id "task-id-1" :latestDownloadCount 1 :execCount 11}
                 {:date "date-2" :task-id "task-id-1" :latestDownloadCount 23 :execCount 11}
                 {:date "date-3" :task-id "task-id-2" :latestDownloadCount 2 :execCount 22}
                 {:date "date-3" :task-id "task-id-2" :latestDownloadCount 2 :execCount 22}
                 {:date "date-3" :task-id "task-id-1" :latestDownloadCount 1 :execCount 22}])

  (print-csv (dwn-count-by-task map-seq1))
  (download-count-freq map-seq1)
  (select-download-count-col map-seq1)
  (dwn-count-by-task map-seq1))