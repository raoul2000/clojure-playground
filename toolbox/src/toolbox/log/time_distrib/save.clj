(ns toolbox.log.time-distrib.save
  (:require [clojure.string :refer [join]]
            [clojure.data.json :as json]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(def supported-output-formats #{:json :csv})

(defn supported-output-format? [format]
  (contains? supported-output-formats format))

(defn supported-output-formats-as-string []
  (->> (map name supported-output-formats)
       (join ", ")))

(defn timestamp->string [[timestamp & remaining]]
  (into (vector (if (instance? String timestamp)
                  timestamp
                  (.format timestamp java.time.format.DateTimeFormatter/ISO_LOCAL_DATE_TIME)))
        remaining))

(defn stringify-timestamp [results]
  (map timestamp->string results))

(defn prepare-for-json [events]
  (map #(update % :results stringify-timestamp) events))

(defn prepare-for-csv [events]
  (->> (mapcat (fn [{:keys [file results]}]
                 (map (fn [[timestamp & match]]
                        (into [file timestamp] match))  results)) events)
       (sort-by second)))

(defn write-csv [target rows]
  (if (= *out* target)
    (csv/write-csv target rows)
    (with-open [writer (io/writer target)]
      (csv/write-csv writer rows))))

(defn write-json [target data]
  (if (= *out* target)
    (json/write data *out*)
    (with-open [writer (io/writer target)]
      (json/write data writer))))

(defn save-events [file format events-coll]
  (case format
    :json (write-json file (prepare-for-json events-coll))
    :csv  (write-csv  file (prepare-for-csv  events-coll))
    (throw (Exception. (str "Unsupported format : " (name format))))))


(comment
  (def date-1 (java.time.LocalDateTime/of 2022 04 21 12 20 11))
  (def date-2 (java.time.LocalDateTime/of 2022 04 22 12 20 11))
  (def events {:file "file1.txt"
               :results [[date-1 "some value 1" "group1" "group2"]
                         [date-1 "some value 2" "groupA" "groupB"]]})
  (def events-2 {:file "file2.txt"
                 :results [[date-1 "xx some value 1" "group1" "group2"]
                           [date-2 "rr" "some value 2" "groupA" "groupB"]]})

  (prepare-for-json events)
  (prepare-for-csv [events])
  (prepare-for-csv [events-2 events])

  (save-events *out* :json [events])
  (save-events  "./test/output/file.json" :json [events])
  (save-events *out* :csv [events])
  (save-events "./test/output/out.csv" :csv [events])
  ;;
  )