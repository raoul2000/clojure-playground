(ns toolbox.log.time-distrib.frequencies
  (:require [clojure.java.io :as io]
            [clojure.data.csv :as csv]))


 ;;(.format timestamp java.time.format.DateTimeFormatter/ISO_LOCAL_DATE_TIME)
 ;;(let [date-formatter (java.time.format.DateTimeFormatter/ofPattern "yyyy MM dd HH mm ss nnn")])

(defn round-timestamp-fn [unit-k]
  (let [unit-m {:day    java.time.temporal.ChronoUnit/DAYS
                :hour   java.time.temporal.ChronoUnit/HOURS
                :minute java.time.temporal.ChronoUnit/MINUTES
                :second java.time.temporal.ChronoUnit/SECONDS
                :nano   java.time.temporal.ChronoUnit/NANOS}]
    (fn [^java.time.LocalDateTime timestamp]
      (.truncatedTo timestamp (get unit-m unit-k)))))

(defn timestamp-coll-mapper [events]
  (map first (:results events)))

(defn occurency-count [[timestamp timestamp-coll]]
  [timestamp (count timestamp-coll)])

(defn report-frequencies [events-coll group-by-k]
  (->> events-coll
       (map timestamp-coll-mapper)             ;; get only timestamps
       flatten                                 ;; a seq of LocalDateTime objects
       (map (round-timestamp-fn  group-by-k))  ;; round-date
       (group-by identity)                     ;; compute ...
       (map occurency-count)                   ;; ... frequencies
       ;;
       ))

(defn to-csv [freq-coll]
  (with-open [writer (io/writer "./test/output/out-file.csv")]
    (csv/write-csv writer [["date_time" "count"]])     ;; columns headers
    (csv/write-csv writer (map identity freq-coll))))  ;; data

(comment
  (def date-1 (java.time.LocalDateTime/of 2022 04 21 11 20 11))
  (def date-2 (java.time.LocalDateTime/of 2022 04 21 12 22 00))
  (def date-3 (java.time.LocalDateTime/of 2022 04 23 14 33 00))
  (def date-4 (java.time.LocalDateTime/of 2022 05 24 14 44 00))

  (def events-1 {:results [[date-1 "some value 1"]
                           [date-2 "some value 2"]
                           [date-3 "some value 3"]]})

  (def events-2 {:results [[date-1 "some value 11"]
                           [date-3 "some value 3"]
                           [date-4 "some value 4"]]})

  (timestamp-coll-mapper events-1)
  (report-frequencies [events-1 events-2] :day)
  (def report (report-frequencies [events-1 events-2] :day))
  (to-csv report)

  (def report-1 {date-4 44
                 date-1 2
                 date-2 4
                 date-3 1})
  (into (sorted-map) report-1)

  ((juxt first last) (keys (into (sorted-map) report-1)))

  (apply min [date-1 date-2 date-3])
  (sort  [date-4 date-1 date-2 date-3])
  ((juxt first last) [1 2 3])


  ;;
  )