(ns metrics.parse
  (:require [clojure.string :as str]))


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
  "parse a string into a seq of maps"
  [s row-def]
  (map #(mapify-line %1 row-def) (str/split s #"\n")))

(defn parse-file
  "parse a file given its path"
  [file row-def]
  (parse-str (slurp file) row-def))
