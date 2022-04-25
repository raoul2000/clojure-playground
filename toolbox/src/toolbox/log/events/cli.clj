(ns toolbox.log.events.cli
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :refer [blank? join lower-case]]
            [babashka.fs :as fs]
            [toolbox.log.events.search :as search]
            [toolbox.log.events.search-output :as search-out]
            [toolbox.log.events.frequencies :as freq]
            [toolbox.log.events.frequencies-output :as freq-out]))

(def action-name "log-events")

(def opt-default-pattern       "*.log")
(def opt-default-output-file   *out*)    ;; stdout
(def opt-default-output-format :csv)

(defn parent-folder-eixsts? [s]
  (if-let [parent (fs/parent s)]
    (fs/exists? parent)
    true))

(defn string->keyword [s]
  (keyword (lower-case s)))

(def cli-options [["-p" "--pattern GLOB" "Glob pattern applied to folder argument to get files to process. Ignored when applied to file argument"
                   :default       opt-default-pattern
                   :validate      [#(not (blank? %)) "Must not be blank"]]


                  ["-o" "--output-file FILE_PATH" "Output file path. If not set output to stdout"
                   :default       opt-default-output-file
                   :default-desc  "stdout"
                   :validate      [#(parent-folder-eixsts? %)
                                   #(str "folder not found: " (fs/parent %))]]

                  ["-f" "--output-format FORMAT" (str "Output format, default to "
                                                      opt-default-output-format ". Supported formats : "
                                                      (search-out/supported-output-formats-as-string))
                   :default       opt-default-output-format
                   :parse-fn      string->keyword
                   :default-desc  "csv"
                   :validate      [#(search-out/supported-output-format?  %)
                                   #(str "Unsupported output format: " %
                                         ". Must be one of "
                                         (search-out/supported-output-formats-as-string))]]

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
  (->> ["Log Event Search & frequency"
        "----------------------------"
        ""
        (str "Processes one or more file searching for an 'event' identified by regular expression match and a timestamp. "
             "Output the result as flat list or aggregated as a frequency list.")
        ""
        (format "Usage: toolbox %s [options] REGEX [file|folder]" action-name)
        ""
        "Options:"
        (:summary parsed-opts)
        ""
        "Example:"
        ""
        "Extract event when string 'marker' is found in a line from the given file and write CSV result to stdout"
        (format "    toolbox %s \".*marker.*\" ./folder/file.log > result.csv" action-name)
        ""
        "Extract event and build frequency report saved as JSON to stdout"
        (format "    toolbox %s --frequency hour --output-format json \".*marker.*\" ./folder/file.log > result.json" action-name)
        ""
        "Extract event from all files with extension log under the given folder and build a day frequency report saved as CSV to file"
        (format "    toolbox %s --frequency day --pattern \"**/*.log\" --output-file result.csv \".*marker.*\" ./folder" action-name)
        ""]
       (join \newline)))

(defn help-option? [parsed-opts]
  (get-in parsed-opts [:options :help]))

(defn output [events {:keys [output-format output-file frequency]}]
  (if frequency
    (->> (freq/create events frequency)
         (freq-out/save output-file output-format))
    (search-out/save-events output-file output-format events)))

(defn run-single [file-path event-re options]
  (let [events-per-file [(search/extract-events file-path event-re)]]
    (output events-per-file options)))

(defn run-multi [folder-path event-re {:keys [pattern] :as options}]
  (let [events-per-file (->> (fs/glob folder-path pattern)
                             (map #(search/extract-events (.toString %) event-re)))]
    (output events-per-file options)))

(defn string->re [s]
  (try
    {:re (re-pattern s)}
    (catch Exception ex {:error (str "Invalid regular expression : " (.getMessage ex))})))

(defn run [args]
  (let [parsed-opts (parse-opts args cli-options)
        arguments   (:arguments parsed-opts)]
    (cond
      (:errors parsed-opts)      (println (join \newline (:errors parsed-opts)))
      (help-option? parsed-opts) (println (usage parsed-opts))
      (zero? (count arguments))  (println "Missing argument. Use '--help' to show usage")
      :else (let [event-re-m   (string->re (first arguments))
                  file-or-path (or (second arguments) ".")
                  options      (:options       parsed-opts)]
              (cond
                (:error event-re-m)             (printf (:error event-re-m))
                (not (fs/exists? file-or-path)) (printf "path not found : %s\n" file-or-path)
                (fs/directory?   file-or-path)  (run-multi  file-or-path (:re event-re-m) options)
                :else                           (run-single file-or-path (:re event-re-m) options))))))

(comment
  (run ["-h"])
  (run [".*event"])

  (run ["--output-format" "csv" ".*event$" "./test/fixture/log/time_distrib/example-1.txt"])

  (run ["--output-file" "./test/output/out.csv"  ".*(event) .*(fffff).*$"
        "./test/fixture/log/time_distrib/example-1.txt"])

  (run ["--pattern"
        "*.txt" ".*(event)$" "./test/fixture/log/time_distrib"])

  (run ["--output-file" "./test/output/evets-3.json" "--pattern"  "*.txt" ".*(event)$" "./test/fixture/log/time_distrib"])
  ;;
  )

