(ns toolbox.log.events.search
  (:require [clojure.java.io :as io]))


;; matches (ex): "2022-04-11 21:56:14,161..."
;; matches (ex): "2022-04-11T21:56:14,161..."
;; matches (ex): "[2022-04-11T21:56:14,161]..."
;; matches (ex): " [ 2022-04-11T21:56:14,161 ]..."
(def default-re-timestamp #"^\s*.?\s*(\d{4})-(\d{2})-(\d{2}).(\d{2}):(\d{2}):(\d{2}),(\d{3}).*")

(defn parse-line-timestamp [line]
  (when-let [[_ & tokens]  (re-matches default-re-timestamp line)]
    (let [[year month day hour min sec msec] (map #(Integer/parseInt %) tokens)]
      (java.time.LocalDateTime/of year month day hour min sec msec))))

(defn parse-line-event [old-value timestamp line re-event]
  (if-let [match (re-matches re-event line)]
    (conj old-value (into [timestamp] (if (vector? match) match [match])))  ;; re with capture group returns an array 
                                                                            ;; re without capturing group returns a string 
    old-value))

(defn event-reducer [re-event]
  (fn [res line]
    (let [timestamp (or (parse-line-timestamp line)
                        (:timestamp res))]
      (-> res
          (assoc  :timestamp  timestamp)
          (update :line-count inc)
          (update :results    parse-line-event timestamp line re-event)))))

(defn extract-events
  [file re-event]
  (-> (with-open [rdr (io/reader file)]
        (doall
         (reduce (event-reducer re-event)  {:line-count 0
                                            :timestamp  nil
                                            :results    []} (line-seq rdr))))
      (assoc  :file (.toString file))
      (dissoc :timestamp)))

(comment
  (extract-events "./test/fixture/log/time_distrib/example-1.txt" #".*(event).*")
  (extract-events "./test/fixture/log/time_distrib/example-1.txt" #".*(XXX).*")
  (extract-events  "./test/fixture/log/time_distrib/example-1.txt" #".*event$")
  ;;
  )


