(ns toolbox.log.time-distrib.save
  (:require [clojure.string :refer [join]]))

(def supported-output-formats #{:json :csv})

(defn supported-output-format? [format]
  (contains? supported-output-formats format))

(defn supported-output-formats-as-string []
  (->> (map name supported-output-formats)
       (join ", ")))