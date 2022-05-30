(ns cli
  (:require [clojure.string    :refer [join]]
            [clojure.tools.cli :refer [parse-opts]]
            [services.todo :refer [validate-working-dir default-base-path]]))

(def default-server-port 8890)

(def cli-options
  [["-p" "--port PORT" "Port number"
    :default  default-server-port
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]

   ["-d" "--working-dir DIR_PATH" "The path to the working directory"
    :default  default-base-path
    :validate [#(validate-working-dir %)]]

   ;; A boolean option defaulting to nil
   ["-h" "--help"]])

(defn usage [parsed-opts]
  (->> [""
        "usage: java -jar server-X.X.X.jar [options]"
        ""
        "Options:"
        (:summary parsed-opts)
        ""]
       (join \newline)))

(defn help-option? [parsed-opts]
  (get-in parsed-opts [:options :help]))

(defn parse-cli-options [args]
  (parse-opts args cli-options :in-order true))

(defn show-errors [errors]
  (->> errors
       (map (partial str "\t- "))
       (join \newline)
       (str "Error:\n")))

(defn cli-opt-working-dir [parsed-options]
  (get-in parsed-options [:options :working-dir]))

(comment
  (parse-cli-options ["--port" "8000"])
  (parse-cli-options ["--working-dir" "dummy"])
  (parse-cli-options ["--working-dir" "README.md"])
  (parse-cli-options []))