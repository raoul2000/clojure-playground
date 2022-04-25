(ns toolbox.log.events.frequencies)

 ;;(.format timestamp java.time.format.DateTimeFormatter/ISO_LOCAL_DATE_TIME)
 ;;(let [date-formatter (java.time.format.DateTimeFormatter/ofPattern "yyyy MM dd HH mm ss nnn")])

(def unit-m {:day    java.time.temporal.ChronoUnit/DAYS
             :hour   java.time.temporal.ChronoUnit/HOURS
             :minute java.time.temporal.ChronoUnit/MINUTES
             :second java.time.temporal.ChronoUnit/SECONDS
             :millis java.time.temporal.ChronoUnit/MILLIS})

(defn valid-time-unit? [k]
  (get unit-m k))

(defn time-unit-str-coll []
  (->> (keys unit-m)
       (map name)))

(defn round-timestamp-fn [unit-k]
  (if-let [chrono-unit (get unit-m unit-k)]
    (fn [^java.time.LocalDateTime timestamp]
      (.truncatedTo timestamp chrono-unit))
    identity))

(defn timestamp-coll-mapper [events]
  (map first (:results events)))

(defn occurency-count [[timestamp timestamp-coll]]
  [timestamp (count timestamp-coll)])

(defn create [events-coll group-by-k]
  (->> events-coll
       (map timestamp-coll-mapper)             ;; get only timestamps
       flatten                                 ;; a seq of LocalDateTime objects
       (map (round-timestamp-fn group-by-k))  ;; round-date to prepare grouping
       frequencies
       ;;(group-by identity)                     ;; compute ...
       ;;(map occurency-count)                   ;; ... frequencies
       ;;(map identity)
       ;;
       ))

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
  (create [events-1 events-2] :minute)

  ;;
  )