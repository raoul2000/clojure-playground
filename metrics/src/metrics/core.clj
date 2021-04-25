(ns metrics.core
  (:require [clojure.string :as str])
  (:gen-class))

(defn str->int
  [str]
  (Integer. (str/trim str)))

; CSV row definition where k is the column name and v the function
; to apply to the string col value to obtain a typed col value
(def csv-row-def
  {:date identity
   :task-id identity
   :latestDownloadCount str->int
   :execCount str->int
   :latestExecDurationMs str->int
   :totalExecDurationMs str->int
   :avgExecDurationMs str->int
   :downloadTotalCount str->int})

(defn add-entry
  "merge the [k v] map entry into map m.
   The pair vector is [:colname  value] where value is a string
   If :colname is found in the conversion map, value is converted"
  [m [k v] row-def]
  (let [convf (get row-def k)]
    (assoc m k ((or convf identity) v))))

(defn mapify-line
  "transform a CSV line into a map given a conversion map describing columns"
  [line row-def]
  (reduce #(add-entry %1 %2 row-def)
          {}
          (map vector (keys row-def) (str/split line #","))))

(defn parse-str
  [s row-def]
  (map #(mapify-line %1 row-def) (str/split s #"\n")))

(defn parse-file
  "parse a file given its path"
  [file row-def]
  (printf "file : %s\n" file)
  (parse-str (slurp file) row-def))


(defn distrib-download-count
  "retains only :task-id and :latestDownloadCount entries and 
   returns the resulting map groupde by :task-id"
  [rec-seq]
  (let [download-count (map #(assoc {}
                                    :task-id (:task-id %)
                                    :latestDownloadCount (:latestDownloadCount %)) rec-seq)]
    (group-by :task-id download-count)))

(defn complete-serie
  "given a map where keys are int, returns a new map
   where all keys between 0 and (max key) are present with a value 
   set to 0 is not already present in m"
  [m]
  (let [max-key (apply max (keys m))
        zero-vals (reduce #(assoc %1 %2 0) {} (range 0 (inc max-key)))]
    (merge zero-vals m)))

(defn freq-download-count
  [map-distrib]
  (reduce-kv  #(assoc %1 %2 (complete-serie (frequencies  (map :latestDownloadCount %3))))
              {}
              map-distrib))

(defn print-dwn-count
  [rec]
  (doseq [i (seq rec)]
    (printf "task : %s\n" (first i))
    (print i)
    
    ))

(defn print-dwn-count-orig
  [rec]
  (doseq [i (seq rec)]
    (printf "task : %s\n" (first i))
    (print i)
    (let [total-run (reduce + (map second (second i)))]
      (println "dwn/run;tot. run;percent run")
      (doseq [cnt (reverse (sort-by second (seq (second i))))]
        (printf "%02d;%02d;%02.2f\n"
                (first cnt)
                (second cnt)
                (float (/ (* 100 (second cnt)) total-run))))
      (printf "total-run = %d\n\n" total-run))))


; (printf "total-download = %d\n" (reduce + (map #(* (first %1) (second %1)) (second i))))
(comment
  (print-dwn-count {"news" {0 4, 7 3, 1 11, 4 1, 6 2, 3 10, 2 13, 9 3, 5 6, 10 8}}))

;total-downloads (reduce + (map #(* (first %1) (second %1)) (second i)))
; (printf "total-download = %d\n\n"  total-downloads)

(defn -main
  [& args]
  (if  (zero? (count args))
    (println "no argument")
    (->>  (apply concat (map #(parse-file %1 csv-row-def) args))
          distrib-download-count
          freq-download-count
          print-dwn-count)))

(comment
  (-main
   "C:/tmp/log-afpapi/metrics/afpapi-gateway_metrics.2021-04-02.0.log"
   "C:/tmp/log-afpapi/metrics/afpapi-gateway_metrics.2021-04-03.0.log"
   "C:/tmp/log-afpapi/metrics/afpapi-gateway_metrics.2021-04-04.0.log"
   "C:/tmp/log-afpapi/metrics/afpapi-gateway_metrics.2021-04-05.0.log"
   "C:/tmp/log-afpapi/metrics/afpapi-gateway_metrics.2021-04-06.0.log"
   "C:/tmp/log-afpapi/metrics/afpapi-gateway_metrics.2021-04-07.0.log"
   "C:/tmp/log-afpapi/metrics/afpapi-gateway_metrics.log")
  (print '(1 2))
  (defn simple [x]
    (+ 1  x))
  (simple 1)
  (def s1 {"news" {0 39, 2 14, 1 22, 3 2}, "photos" {0 4, 7 3, 1 11, 4 1, 6 2, 3 10, 2 13, 9 3, 5 6, 10 8}})
  (reduce + (map #(* (first %1) (second %1)) {1 1 2 3 4 5}))
  (reduce-kv assoc {} s1)
  (reduce-kv #(assoc %1 %2 %3) {} s1)

  (let [vals {0 39, 2 14, 1 22, 5 2}
        max-key (apply max (keys vals))
        def-val (reduce #(assoc %1 %2 0) {} (range 0 (inc max-key)))]
    (println max-key)
    (println def-val)

    (merge def-val vals))

  (complete-serie {0 39, 2 14, 1 22, 5 2})
  (reduce #(assoc %1 %2 0) {} (range 0 11))
  (into {} {:a 1 :b 2})
  (apply max '(1 5 9))
  (apply max (keys {0 0 1 1 22 2}))

  (reduce-kv #(assoc %1 %2
                     (into
                      (reduce-kv assoc {} (range 0 (inc (apply max (keys %3)))))
                      %3)) {} s1)

  (defn f1
    [a [b c] d]
    (println a b c d))

  (f1 1 [2 3] 4)
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

  (parse-file "./test/fixtures/metrics-1.csv" csv-row-def)

  (time (-main "C:/dev/ws/lab/clojure-playground/metrics/test/fixtures/metrics-1.csv"
               "C:/dev/ws/lab/clojure-playground/metrics/test/fixtures/metrics-2.csv"))


  (parse-str "a,1\nb,2" csv-row-def)
  (count (parse-file "./test/fixtures/metrics-1.csv" csv-row-def))
  (count (parse-file "./test/fixtures/metrics-2.csv" csv-row-def))
  (time (distrib-download-count (parse-file "./test/fixtures/metrics-1.csv" csv-row-def)))
  (freq-download-count (distrib-download-count (parse-file "./test/fixtures/metrics-2.csv" csv-row-def)))

  (->> (parse-file "./test/fixtures/metrics-2.csv" csv-row-def)
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