(ns toolbox.tools
  (:require [clojure.string :refer [join]]
            [clojure.tools.cli :refer [parse-opts]]
            [babashka.fs :as fs]
            [toolbox.depend.cli :as cli-depend]
            [toolbox.log.events.cli :as cli-events]
            [toolbox.dispatch-cmd.cli :as cli-dispatch-cmd])
  (:gen-class))

(def cli-options [["-h" "--help" "Show usage"]])

(defn usage [parsed-opts]
  (->> [""
        "Toolbox"
        "======="
        ""
        "usage: toolbox [options]"
        "       toolbox ACTION [action-options...]"
        ""
        "Options:"
        (:summary parsed-opts)
        ""
        "Actions:"
        (str "  " cli-depend/action-name)
        (str "  " cli-events/action-name)
        (str "  " cli-dispatch-cmd/action-name)
        ""
        "Use 'toolbox ACTION --help' to get help about an action"
        ""]
       (join \newline)))

(defn help-option? [parsed-opts]
  (get-in parsed-opts [:options :help]))

(defn -main [& args]
  (let [parsed-opts (parse-opts args cli-options :in-order true)
        action      (first (:arguments parsed-opts))
        action-opts (rest  (:arguments parsed-opts))]
    (cond
      (help-option? parsed-opts)               (println (usage parsed-opts))
      (nil? action)                            (println "action is missing. Use --help to show usage")
      (= cli-depend/action-name action)        (cli-depend/run action-opts)
      (= cli-events/action-name action)        (cli-events/run action-opts)
      (= cli-dispatch-cmd/action-name action)  (cli-dispatch-cmd/run action-opts)
      :else                                    (printf "unknown action: %s. Use --help to show usage\n" action))
    (flush)))

(comment
  (-main "depend" "-h")
  (-main "log-events" "-h")
  (-main "--help")
  (-main "dispatch-cmd" "-h")
  (-main "dispatch-cmd" "-p" "80" "--targets" "127.0.0.1" "ls")
  (-main "dispatch-cmd" "-p" "80" "--targets" "username@127.0.0.1" "ls")
  (-main)
  ;;       

  ;;
  )
