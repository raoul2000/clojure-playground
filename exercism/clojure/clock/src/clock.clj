(ns clock
  (:import [java.time LocalTime]))

(defn clock->string [clock]
  (.toString clock))

(defn clock [hours minutes]
  (.plusMinutes (LocalTime/MIDNIGHT)  (+ (* hours 60) minutes)))

(defn add-time [clock time]
  (.plusMinutes clock time))
