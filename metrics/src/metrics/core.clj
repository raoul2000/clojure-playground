(ns metrics.core
  (:require [clojure.string :as str]
            [metrics.parse]
            [metrics.dwn-count])
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

(defn print-footer
  [args]
  (printf "files:\n")
  (doseq [filename args] (println filename)))

(defn -main
  [& args]
  (if  (zero? (count args))
    (println "no argument")
    (do
      (->>  args
            (map #(metrics.parse/parse-file %1 csv-row-def))
            (apply concat)
            metrics.dwn-count/report)
      (print-footer args)
      (flush))))

(comment
  (time (-main
         "C:/tmp/log-afpapi/metrics/afpapi-gateway_metrics.2021-04-02.0.log"
         "C:/tmp/log-afpapi/metrics/afpapi-gateway_metrics.2021-04-03.0.log"
         "C:/tmp/log-afpapi/metrics/afpapi-gateway_metrics.2021-04-04.0.log"
         "C:/tmp/log-afpapi/metrics/afpapi-gateway_metrics.2021-04-05.0.log"
         "C:/tmp/log-afpapi/metrics/afpapi-gateway_metrics.2021-04-06.0.log"
         "C:/tmp/log-afpapi/metrics/afpapi-gateway_metrics.2021-04-07.0.log"
         "C:/tmp/log-afpapi/metrics/afpapi-gateway_metrics.log"))
  )
