(ns toolbox.tools
  (:require [clojure.string :refer [blank? join]]
            [clojure.tools.cli :refer [parse-opts]]
            [babashka.fs :as fs]
            [toolbox.depend.cli :as cli-depend])
  (:gen-class))

(def cli-options [["-h" "--help" "Show usage"]])

(defn usage [parsed-opts]
  (->> ["Toolbox"
        "======="
        ""
        "usage: toolbox [options]"
        "       toolbox ACTION [action-options...]"
        ""
        "Options:"
        (:summary parsed-opts)
        ""
        "Actions:"
        "  depend"
        "  more to come ..."
        ""
        "tips: toolbox ACTION --help"]
       (join \newline)))

(defn help-option? [parsed-opts]
  (get-in parsed-opts [:options :help]))

(defn -main [& args]
  (let [parsed-opts (parse-opts args cli-options :in-order true)
        action      (first (:arguments parsed-opts))
        action-opts (rest  (:arguments parsed-opts))]
    (cond
      (help-option? parsed-opts) (println (usage parsed-opts))
      (nil? action)              (prn "action is missing. Use --help to show usage")
      (= "depend" action)        (cli-depend/run action-opts)
      :else                      (printf "unknown action: %s" action))))

(comment
  (-main "depend" "-h")
  (-main "--help")

  ;; depend /d/a **/*.bash /d/a tgf
  ;; depend  **/*.bash /d/a tgf =>  depend  ./ **/*.bash /d/a tgf
  ;; depend /d/a/script.bash /d/a tgf
  ;; depend /d/a/script.bash /d/a 
  ;; depend /d/a/script.bash  => depend /d/a/script.bash  ./


  ;; depend --input-file "./e/d/script.bash"
  ;; depend --input-file "./e/d/script.bash" --source-dir "./e"
  ;; depend --input-dir "./e/d/h"
  ;; depend --input-dir "./e/d/h"" --pattern "**/*.bash"


  ;;       

  (def cli-options

    [["-s" "--input-script FILE_PATH" "Script path"
      :validate [#(fs/regular-file? %) "Must be an existing file path"]]

     ["-d" "--input-dir DIR_PATH" "Path to the directory to process"
      :validate [#(fs/directory? %) "Must be an existing directory path"]]

     ["-p" "--pattern GLOB" "Glob pattern applied to --input-dir to get files to process"
      :default "*.*"
      :validate [#(not (blank? %)) "Must not be blank"]]

     ["-r" "--root-dir DIR_PATH" "Path to the source root directory"
      :validate [#(fs/directory? %) "Must be an existing directory path"]]

     ["-o" "--output-file FILE_PATH" "Output file path. If not specified, output to stdout"]

     ["-h" "--help"]])

  (fs/exists? "c:/tmp")
  (fs/regular-file? "./resources/test/root/start.bash")
  (parse-opts [] cli-options)
  (parse-opts ["-s" "./resources/test/root"] cli-options)
  (parse-opts ["-s" "./resources/test/root/start.bash"] cli-options)
  (parse-opts ["-p"] cli-options)
  (parse-opts ["-p" "**/*.bash"] cli-options)
  (parse-opts ["-h"] cli-options)
  (parse-opts ["-h" "some"] cli-options)
  (parse-opts ["depend" "-some"] [] :in-order true)

  ;;
  )
