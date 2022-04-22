(ns toolbox.log.time-distrib.cli
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :refer [blank? join lower-case]]
            [babashka.fs :as fs]
            [toolbox.log.time-distrib.core :as core]
            ;;[toolbox.log.time-distrib.frequencies :refer [report-frequencies valid-time-unit? time-unit-str-coll]]
            [toolbox.log.time-distrib.frequencies :as freq]
            [toolbox.log.time-distrib.save :refer [save-events supported-output-format? supported-output-formats-as-string]]))

(def opt-default-pattern       "*.log")
(def opt-default-output-file   *out*)  ;; stdout
(def opt-default-output-format :csv)

(defn parent-folder-eixsts? [s]
  (if-let [parent (fs/parent s)]
    (fs/exists? parent)
    true))

(defn string->keyword [s]
  (keyword (lower-case s)))

(def cli-options [["-p" "--pattern GLOB" "Glob pattern applied to folder to get files to process. Ignored when applied to file"
                   :default       opt-default-pattern
                   :validate      [#(not (blank? %)) "Must not be blank"]]


                  ["-o" "--output-file FILE_PATH" "Output file path. If not set output to stdout"
                   :default       opt-default-output-file
                   :default-desc  "stdout"
                   :validate      [#(parent-folder-eixsts? %)
                                   #(str "folder not found: " (fs/parent %))]]

                  ["-f" "--output-format FORMAT" (str "Output format, default to "
                                                      opt-default-output-format ". Supported formats : "
                                                      (supported-output-formats-as-string))
                   :default       opt-default-output-format
                   :default-desc  "csv"
                   :validate      [#(supported-output-format?  %)
                                   #(str "Unsupported output format: " %
                                         ". Must be one of "
                                         (supported-output-formats-as-string))]]

                  ["-r" "--frequency TIME_UNIT" "Output event frequencies by time unit"
                   :parse-fn      string->keyword
                   :validate      [freq/valid-time-unit? (fn [_] (str "time unit must be one of : "
                                                                 (join ", " (freq/time-unit-str-coll))))]]

                  ["-h" "--help"]])

(comment
  (print (:summary (parse-opts [] cli-options)))
  (parse-opts [] cli-options)
  (parse-opts ["-e" ""] cli-options)
  (parse-opts ["-e" "*ee"] cli-options)
  (parse-opts ["-e" ".*ee"] cli-options)
  (parse-opts ["--frequency" "day"] cli-options)
  (parse-opts ["--frequency" "dayzzz"] cli-options)
  ;;
  )

(defn usage [parsed-opts]
  (->> ["Log Event Time Distribution"
        ""
        "Usage: toolbox log-time-distrib [options] REGEX [file|folder]"
        ""
        "Options:"
        (:summary parsed-opts)
        ""]
       (join \newline)))

(defn help-option? [parsed-opts]
  (get-in parsed-opts [:options :help]))

(defn output [events {:keys [output-format output-file frequency]}]
  (if frequencies
    (->> (freq/create events frequency)
         (freq/save-as output-file output-format))
    (save-events output-file output-format events)))


(defn run-single [file-path event-re options]
  (let [events [(core/extract-events file-path event-re)]]
    (prn events)
    (output events options)
    ;;(save-events output-file events)
    ))

(defn run-multi [folder-path event-re {:keys [pattern] :as options}]
  (let [events (->> (fs/glob folder-path pattern)
                    (map #(core/extract-events (.toString %) event-re)))]
    (prn events)
    (output events options)
    ;;(save-events output-file events)
    ;;
    ))

(defn string->re [s]
  (try
    {:re (re-pattern s)}
    (catch Exception ex {:error (str "Invalid regular expression : " (.getMessage ex))})))

(defn run [args]
  (let [parsed-opts (parse-opts args cli-options)]
    (if (help-option? parsed-opts)
      (println (usage parsed-opts))
      (let [event-re-m    (string->re (first (:arguments parsed-opts)))
            file-or-path  (or (second (:arguments parsed-opts))
                              ".")
            options       (:options       parsed-opts)]
        (cond
          (:error event-re-m)                (printf (:error event-re-m))
          (not (fs/exists? file-or-path))    (printf "path not found : %s\n" file-or-path)
          (fs/directory?   file-or-path)     (run-multi  file-or-path (:re event-re-m) options)
          :else                              (run-single file-or-path (:re event-re-m) options))))))

(comment
  (run [".*event"])
  (spit *out* "hello")
  (run [".*event$" "./test/fixture/log/time_distrib/example-1.txt"])
  (run ["--output-file" "./test/output/evets-2.json"  ".*(event) .*(fffff).*$" "./test/fixture/log/time_distrib/example-1.txt"])

  (run ["--output-file" "./test/output/evets-3.json" "--pattern"  "*.txt" ".*(event)$" "./test/fixture/log/time_distrib"])
  (run ["--output-file" "./test/output/evets-3.json" "--pattern"  "*.txt" ".*(event)$" "./test/fixture/log/time_distrib"])
  ;;
  )
