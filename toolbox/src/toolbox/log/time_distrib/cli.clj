(ns toolbox.log.time-distrib.cli
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :refer [blank? join]]
            [babashka.fs :as fs]
            [toolbox.log.time-distrib.save :refer [supported-output-format? supported-output-formats-as-string]]))

(def opt-default-pattern       "*.log")
(def opt-default-output-file   *out*)  ;; stdout
(def opt-default-output-format :csv)

(defn parent-folder-eixsts? [s]
  (if-let [parent (fs/parent s)]
    (fs/exists? parent)
    true))

(def cli-options [["-p" "--pattern GLOB" "Glob pattern applied to folder to get files to process. Ignored when applied to file"
                   :default   opt-default-pattern
                   :validate [#(not (blank? %)) "Must not be blank"]]

                  ["-o" "--output-file FILE_PATH" "Output file path. If not set output to stdout"
                   :default       opt-default-output-file
                   :default-desc "stdout"
                   :validate [#(parent-folder-eixsts? %)
                              #(str "folder not found: " (fs/parent %))]]

                  ["-f" "--output-format FORMAT" (str "Output format, default to " 
                                                      opt-default-output-format ". Supported formats : "
                                                      (supported-output-formats-as-string))
                   :default  opt-default-output-format
                   :validate [#(supported-output-format?  %)
                              #(str "Unsupported output format: " %
                                    ". Must be one of "
                                    (supported-output-formats-as-string))]]

                  ["-h" "--help"]])

(defn usage [parsed-opts]
  (->> ["Log Event Time Distribution"
        ""
        "Usage: toolbox log-time-distrib [options] [file|folder]"
        ""
        "Options:"
        (:summary parsed-opts)
        ""]
       (join \newline)))

(defn help-option? [parsed-opts]
  (get-in parsed-opts [:options :help]))



