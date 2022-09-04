(ns toolbox.dispatch-cmd.cli
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as s]
            [toolbox.dispatch-cmd.core :as core]
            [clojure.test :as t]))

(def action-name "dispatch-cmd")
(def default-target-host-list-separator ",")

(def cli-options
  "Configure CLI options"
  [["-p" "--port port" "ssh port number"
    :default 22
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "must be a number between 0 and 65536"]]

   [nil "--password pwd"  "ssh login password used for all target that don't have a specific password"
    :default nil]

   ["-t" "--targets list" "list of connexion string to target host where the command is executed"
    :parse-fn #(s/split % #",")
    :validate [#(pos-int? (count %)) "no target host provided"]]

   ["-h" "--help"]])

(comment
  (parse-opts ["-h"] cli-options)
  (parse-opts ["--password" "PWD"] cli-options)
  (parse-opts ["--password=PWD"] cli-options)
  (parse-opts ["--targets=zzz,rrr" "ee"] cli-options)
  (parse-opts ["--targets=zzz,rrr" "ls \"-rtl\""] cli-options)
  (parse-opts ["--password" "PWD"  "--targets=zzz,rrr" "ls \"-rtl\"" "other"] cli-options)

  ;;
  )

(defn usage
  "print usage message"
  [options-summary]
  (->> [""
        "Run the same command on several hosts"
        ""
        "java -jar sniff.X.X.X.jar [options] host"
        ""
        "Options:"
        options-summary
        ""
        "Examples:"
        ""
        " Process host given its IP address, prompt for username and password and output extracted data to JSON file :"
        "      java -jar sniff-X.X.X.jar --targets=pwd1:user1@host_a,pwd2:user2@host_b \"ls -rtl\""
        ""
        ""]
       (s/join \newline)))

(defn help-option? [parsed-opts]
  (get-in parsed-opts [:options :help]))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (s/join \newline errors)))

(defn read-password
  "Display *prompt* message to the console and wait for user input which
   **are not** echoed to screen. 

   If invoked from `user` namespace, user input is echoed to screen.
   
   Returns the user input."
  [prompt]
  ;; from https://gist.github.com/ampersanda/4647d16a5d335dce8dcd49ff47fd851e
  ;; Based on https://groups.google.com/forum/#!topic/clojure/ymDZj7T35x4
  (if (= "user" (str (.getName *ns*)))
    (do
      (print (format "%s [will be echoed to the screen]" prompt))
      (flush)
      (read-line))
    (let [console (System/console)
          chars   (.readPassword console "%s" (into-array [prompt]))]
      (apply str chars))))

(defn read-line-prompt
  "display *prompt* message in the console and wait for
   user input. 
   
   Returns user input."
  [prompt]
  (print (format "%s" prompt))
  (flush)
  (read-line))


(defn parse-connexion-string-1
  "Given a connexion string, returns a map describing the target.
   
   The connexion string must have the following form: `[PASSWORD:]USERNAME@HOST[:PORT]`
   
   Example :
   - user1@my-host
   - user1@my-host:23
   - secret-pwd:username@my-host
   - secret-pwd:username@jump1@jump2@my-host
   "
  [s]
  (let [[_ user host]           (re-matches #"(.*)@([^@]+)$" s)
        [_ password-field username]   (when user (re-matches #"(.+:)?(.+)" user))
        password                      (when password-field (s/replace password-field #":" ""))]
    {:label    (str username "@" host)
     :host     host
     :username username
     :password password
     :value    s}  ;; always set
    ))


(defn parse-connexion-string
  "Given a connexion string, returns a map describing the target.
   
   The connexion string must have the following form: `[PASSWORD:]USERNAME@HOST[:PORT]`
   
   Example :
   - user1@my-host
   - user1@my-host:23
   - secret-pwd:username@my-host
   - secret-pwd:username@jump1@jump2@my-host:23
   "
  [s]
  (let [[_ user+maybe-password host+maybe-port] (re-matches #"(.*)@([^@]+)$" s)
        [_ host _ port]                         (when host+maybe-port  (re-matches #"([^:]+)(:(\d+))?$" host+maybe-port))
        [_ _ password username]                 (when user+maybe-password (re-matches #"((.+):)?(.+)" user+maybe-password))]
    {:label    (str username "@" host)
     :host     host
     :port     port
     :username username
     :password password
     :value    s}  ;; always set
    ))

(comment

  (parse-connexion-string "pwd:name@jump1@jump2:33")
  (parse-connexion-string "pwd:name@jump1@jump2")
  (parse-connexion-string "name@jump1@jump2")
  (parse-connexion-string "name@jump1@jump2:33")
  ;;
  )



(defn parse-target-list
  "Given a comma separated list of target connexion strings, returns a list of
   target maps or *nil* when *targets* is blank."
  [targets]
  (map parse-connexion-string targets))

(comment
  (list? nil)
  (parse-target-list nil)
  (parse-target-list "some string")
  (parse-target-list "aa@bb,cc@dd")
  (parse-target-list "aabbcc@dd")
  (parse-target-list "aa@bb,ccdd")
  (parse-target-list "aa@bb,dlkk:user@jump1@jump2@jump3")
  ;;
  )

(defn apply-default
  "Set the given default *password* and *port* value to all targets in *coll* with *nil* values for these keys.
   If a default property is nil, target property is not modified."
  [coll password port]
  (cond->> coll
    password (map #(update % :password (fnil identity password)))
    port     (map #(update % :port     (fnil identity port)))))

(comment
  (apply-default [{} {}] nil 22)
  (apply-default [{:password nil} {}] nil 22)
  (apply-default [{:password nil} {}] nil 22)
  (apply-default [{} {}] "pwd" 22)
  (apply-default [{} {:password "p"}] "pwd" 22)
  (apply-default [{} {:password "" :port 55}] "pwd" 22)
  ;;
  )

(defn validate-target-reducer
  "Reducer validation for target map.
   
   A target is valid is it has a non blank value for keys `:host` and `:username`"
  [report target]
  (let [label         (:value target)
        error-message #(conj %1 (str "Error (" label ") :  " %2))]
    (cond-> report
      (s/blank? (:host target))     (error-message "missing host")
      (s/blank? (:username target)) (error-message "missing username")
      (s/blank? (:password target)) (error-message "missing password")
      (< 0 (:port target) 0x10000)  (error-message "missing port"))))

(defn validate-target-list [target-list]
  (reduce validate-target-reducer [] target-list))

(comment
  (validate-target-reducer [] {:host "non" :value "username@non"})
  (validate-target-reducer [] {:host "non" :username "bob" :password "pwd" :port "22" :value "username@non"})

  ;;
  )

(defn run [args]
  (let [parsed-opts                                  (parse-opts args cli-options)
        {:keys [port password targets]} (:options parsed-opts)
        cmd                                             (:arguments parsed-opts)
        target-list                                     (parse-target-list targets)]
    (cond
      (:errors parsed-opts)      (println (s/join \newline (:errors parsed-opts)))
      (help-option? parsed-opts) (println (usage parsed-opts))
      (empty? cmd)               (println "missing command")
      (empty? target-list)       (println "missing or invalid target list")
      :else                      (let [final-target-list  (apply-default target-list password port)
                                       target-list-errors (validate-target-list final-target-list)]
                                   (if target-list-errors
                                     (doseq [error-message target-list-errors]
                                       (println error-message))
                                     (core/run-cmd-coll final-target-list (first cmd)))))))