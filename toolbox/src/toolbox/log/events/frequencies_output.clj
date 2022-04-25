(ns toolbox.log.events.frequencies-output
  (:require [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [clojure.data.json :as json]))

(defn write-csv [target rows]
  (if (= *out* target)
    (csv/write-csv target rows)
    (with-open [writer (io/writer target)]
      (csv/write-csv writer rows))))

(defn prepare-for-csv [freq-coll]
  (map identity freq-coll))

(defn write-json [target data]
  (if (= *out* target)
    (json/write data *out*)
    (with-open [writer (io/writer target)]
      (json/write data writer))))

(defn prepare-for-json [freq-coll]
  (->> freq-coll
       (map (fn [[timestamp freq-val]]
              [(.toString timestamp) freq-val]))
       (into {})))

(defn save [file format freq-coll]
  (case format
    :json (write-json file (prepare-for-json freq-coll))
    :csv  (write-csv  file (prepare-for-csv freq-coll))
    (throw (Exception. (str "Unsupported format : " (name format))))))
