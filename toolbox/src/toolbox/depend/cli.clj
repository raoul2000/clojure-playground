(ns toolbox.depend.cli
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :refer [blank? join]]
            [babashka.fs :as fs]
            [toolbox.depend.core :refer [create-deps-tree]]
            [toolbox.depend.save :refer [supported-output-format? save-deps]]))

(def action-name "depend")
(def opt-default-pattern       "*.bash")
(def opt-default-source-dir    ".")
(def opt-default-output-file   *out*)  ;; stdout
(def opt-default-output-format "json")

(defn parent-folder-eixsts? [s]
  (if-let [parent (fs/parent s)]
    (fs/exists? parent)
    true))

(def cli-options [["-p" "--pattern GLOB" "Glob pattern applied to folder to get files to process. Ignored when applied to file"
                   :default   opt-default-pattern
                   :validate [#(not (blank? %)) "Must not be blank"]]

                  ["-s" "--source-dir DIR_PATH" "Path to the source root directory. If not set, use the current dir"
                   :default  opt-default-source-dir
                   :validate [#(fs/directory? %) "Must be an existing directory path"]]

                  ["-o" "--output-file FILE_PATH" "Output file path. If not set output to stdout"
                   :default       opt-default-output-file
                   :default-desc "stdout"
                   :validate [#(parent-folder-eixsts? %)
                              #(str "folder not found: " (fs/parent %))]]

                  ["-f" "--output-format [json | tgf]" (str "Output format, default to " opt-default-output-format)
                   :default  opt-default-output-format
                   :validate [#(supported-output-format?  %)
                              #(str "Unsupported output format: " %)]]

                  ["-h" "--help"]])


(defn usage [parsed-opts]
  (->> ["Explore dependencies"
        "--------------------"
        ""
        (format "Usage: toolbox %s [options] [file|folder]" action-name)
        ""
        "Options:"
        (:summary parsed-opts)
        ""
        "Example:"
        ""
        "Process script, output result to standard output"
        (format "    toolbox %s ./folder/script.bash" action-name)
        ""
        "Process files in folder /folder with extension txt. Write tgf result to out.tgf"
        (format "    toolbox %s --pattern \"*.txt\" /folder > out.tgf" action-name)
        ""
        "Process script and output json result. Dependencies files will be searched under folder ./src"
        (format "    toolbox %s --source-dir ./src --output-format json ./src/folder/script.bash" action-name)
        ""
        "Process script and output json result. Dependencies files will be searched under folder ./src"
        "Output JSON result to file dependencies.json"
        (format "    toolbox %s --source-dir ./src --output-file dependencies.json --output-format json ./src/folder/script.bash" action-name)
        ""]
       (join \newline)))

(defn help-option? [parsed-opts]
  (get-in parsed-opts [:options :help]))

(defn run-single [file-path source-dir output-file output-format]
  (save-deps output-file
             (create-deps-tree file-path source-dir)
             output-format))

(defn run-multi [folder-path glob-pattern source-dir output-file output-format]
  (let [deps-coll (->> (fs/glob folder-path glob-pattern)
                       (map #(create-deps-tree (.toString %) source-dir)))]
    (save-deps output-file
               deps-coll
               output-format)))

(defn run [args]
  (let [parsed-opts (parse-opts args cli-options)]
    (cond
      (:errors parsed-opts)      (println (join \newline (:errors parsed-opts)))
      (help-option? parsed-opts) (println (usage parsed-opts))
      :else (let [file-or-path  (or (first (:arguments parsed-opts))
                                    ".")
                  options       (:options       parsed-opts)
                  glob-pattern  (:pattern       options)
                  source-dir    (:source-dir    options)
                  output-file   (:output-file   options)
                  output-format (:output-format options)]
              (cond
                (not (fs/exists? file-or-path))    (printf "path not found : %s\n" file-or-path)
                (fs/directory?   file-or-path)     (run-multi  file-or-path glob-pattern source-dir output-file output-format)
                :else                              (run-single file-or-path source-dir output-file output-format))))))

(comment
  ;; process all files in current dir matching default pattern
  (parse-opts [] cli-options)
  (run [])
  (run ["-h"])
  (run ["test/fixture/root-1/start.bash"])
  (run ["--output-format" "tgf"  "test/fixture/root-1/start.bash"])
  (run ["test/fixture/root-1"])
  (run ["--pattern"  "**.bash" "test/fixture/root-1"])
  (run ["--pattern"  "*.bash"
        "--source-dir" "./resources"
        "--output-file" "./resources/test/out1.tgf"
        "--output-format" "tgf"
        "test/fixture/root-1"])
  (run ["--pattern" "*.txt"  "test/fixture/root-1"])
  (run ["/not/found"])
  (run ["/not/found.txt"])


  (parse-opts ["-h"] cli-options)
  ;; process all files in current dir matching pattern *.txt

  (parse-opts ["-p" "*.txt"] cli-options)

  ;; process script.bash in current folder
  (parse-opts ["script.bash"] cli-options)

  ;; process ./a/b/script.bash
  (parse-opts ["./a/b/script.bash"] cli-options)

  ;; process all files in folder ./a/b matching default pattern
  (parse-opts ["./a/b"] cli-options)

  ;; process all files in folder ./a/b matching pattern *.txt
  (parse-opts ["-p" "*.txt"  "./a/b"] cli-options)


  (parse-opts ["--output-format" "tgf"  "./a/b"] cli-options)

  (parse-opts ["--output-file" "a/t.txt"  "./a/b"] cli-options)

  ;;
  )

