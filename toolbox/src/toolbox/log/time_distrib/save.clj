(ns toolbox.log.time-distrib.save
  (:require [clojure.string :refer [join]]
            [clojure.data.json :as json]))

(def supported-output-formats #{:json :csv})

(defn supported-output-format? [format]
  (contains? supported-output-formats format))

(defn supported-output-formats-as-string []
  (->> (map name supported-output-formats)
       (join ", ")))

(defn timestamp->string [[timestamp & remaining]]
  (vector
   (if (instance? String timestamp)
     timestamp
     (.format timestamp java.time.format.DateTimeFormatter/ISO_LOCAL_DATE_TIME))
   remaining))

(defn stringify-timestamp [results]
  (map timestamp->string results))

(defn events->json [events]
  (->> events
       (map #(update % :results stringify-timestamp))
       (json/write-str)))

(defn save-events [file events]
  (spit file (events->json events)))

(comment
  (def date-1 (java.time.LocalDateTime/of 2022 04 21 12 20 11))
  (def events {:results [[date-1 "some value 1"]
                         [date-1 "some value 2"]]})

  (events->json events)
  (save-events *out* events)

  (java.time.format.DateTimeFormatter/ofPattern "yyyy MM dd HH mm ss nnn")

  (java.time.format.DateTimeFormatter/ISO_DATE)

  (.format date-1 java.time.format.DateTimeFormatter/ISO_LOCAL_DATE_TIME)

  (update events :results #(map (fn [[timestamp & remaining]]
                                  (vector
                                   (.format timestamp java.time.format.DateTimeFormatter/ISO_LOCAL_DATE_TIME)
                                   remaining)) %)))