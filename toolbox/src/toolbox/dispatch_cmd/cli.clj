(ns toolbox.dispatch-cmd.cli
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as s]
            [toolbox.dispatch-cmd.core :as core]
            [clojure.test :as t]))

(def action-name "dispatch-cmd")
(def default-target-host-list-separator ",")

(defn valid-port?  [s]
  (boolean (and  (string? s)
                 (re-matches #"\d+" s)
                 (< 0 (Integer/parseInt s) 0x10000))))


(def cli-options
  "Configure CLI options"
  [["-p" "--port port" "ssh port number"
    :default "22"
    :validate [valid-port? "must be a number between 0 and 65536"]]

   [nil "--password pwd"  "default password used for all targets that don't have a specific password"
    :default nil]

   ["-t" "--targets list" "Comma separated list of connexion strings to target hosts where the command will be executed"
    :parse-fn #(map s/trim (s/split % #","))
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
        (:summary options-summary)
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
  (if (= "toolbox.dispatch-cmd.cli" (str (.getName *ns*)))
    (do
      (print (format "%s [will be echoed to the screen]" prompt))
      (flush)
      (read-line))
    (let [console (System/console)
          chars   (.readPassword console "%s" (into-array [prompt]))]
      (apply str chars))))

(defn read-password-or-skip
  "Returns user input string or *nil* if blank."
  [prompt]
  (let [text (read-password prompt)]
    (when-not (s/blank? text) text)))

(defn read-line-prompt
  "display *prompt* message in the console and wait for
   user input. 
   
   Returns user input."
  [prompt]
  (print (format "%s" prompt))
  (flush)
  (read-line))


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
        [_ host _ port]                         (when host+maybe-port     (re-matches #"([^:]+)(:(\d+))?$" host+maybe-port))
        [_ _ password username]                 (when user+maybe-password (re-matches #"((.+):)?(.+)"      user+maybe-password))]
    {:label    (str username "@" host)
     :host     host
     :port    (when port (Integer/parseInt port))
     :username username
     :password password
     :value    s}  ;; always set TODO: remove because risk expose password
    ))

(comment

  (parse-connexion-string "pwd:name@jump1@jump2:33")
  (parse-connexion-string "pwd:name@jump1@jump2")
  (parse-connexion-string "name@jump1@jump2")
  (parse-connexion-string "name@jump1@jump2:33")
  (parse-connexion-string ":username@host:22")

  ;;
  )

(defn parse-cnx-string [s]
  (let [[password user+host port] (s/split s #":")])


  ;;
  )

(comment
  (s/split "pwd:@host:22" #":")
  (s/split "pwd:@host" #":")
  (s/split "username@host:22" #":")
  (s/split "username@host" #":")
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
  (parse-target-list ["some string"])
  (parse-target-list ["aa@bb" "cc@dd"])
  (parse-target-list ["aabbcc@dd"])
  (parse-target-list ["aa@bb" "ccdd"])
  (parse-target-list ["aa@bb" "dlkk:user@jump1@jump2@jump3"])
  ;;
  )

(defn apply-default
  "Set the given default *password* and *port* value to all targets in *coll* with *nil* values for these keys.
   If a default property is nil, target property is not modified."
  [coll default-password default-port]
  (cond->> coll
    default-password (map #(update % :password (fnil identity default-password)))
    default-port     (map #(update % :port     (fnil identity default-port)))))

(comment
  (apply-default [{} {}] nil 22)
  (apply-default [{:password nil} {}] nil 22)
  (apply-default [{:password nil} {}] nil 22)
  (apply-default [{} {}] "pwd" 22)
  (apply-default [{} {:password "p"}] "pwd" 22)
  (apply-default [{} {:password "" :port 55}] "pwd" 22)
  ;;
  )

(defn prompt-missing-password [coll]
  (map (fn [target]
         (update target :password (fn [cur-pwd]
                                    (if-not (s/blank? cur-pwd)
                                      cur-pwd
                                      (read-password-or-skip (str "target:" (:label target)
                                                                  "\nenter password")))))) coll))

(comment
  (prompt-missing-password [{:a 1 :password nil}])
  (prompt-missing-password [{:a 1 :password  ""}])
  (prompt-missing-password [{:a 1 :password  "dd"}])
  (prompt-missing-password [{:a 1 :label "LAB"} {:a 1 :label "LA2"}])
  ;;
  )

(defn missing-password?
  "Given a *coll* of target map, returns TRUE if at least one target has no password value"
  [coll]
  (some (comp s/blank? :password) coll))

(comment
  (missing-password? [{:password ""} {:password "pwd"}])
  (missing-password? [{:password nil} {:password "pwd"}])
  (missing-password? [{:password "de"} {:password "pwd"}])
  ;;
  )

(defn fill-missing-keys [target-list password-opt port]
  (let [default-password (if (and (s/blank? password-opt)
                                  (missing-password? target-list))
                           (read-password-or-skip (str "enter default password (ENTER to skip)"))
                           password-opt)]
    (->> (apply-default target-list default-password port)
         (prompt-missing-password))))

(comment
  (fill-missing-keys [{:label "A" :password ""}] "" 22)
  (fill-missing-keys [{:label "A" :password nil}] "" 22)

  ;;
  )

(defn validate-target-reducer
  "Reducer validation for target map.
   
   A target is valid is it has a non blank value for keys `:host` and `:username`"
  [report target]
  (let [label         (:value target)
        error-message #(conj %1 (str "Error (" label ") :  " %2))]
    (cond-> report
      (s/blank? (:host target))          (error-message "missing host")
      (s/blank? (:username target))      (error-message "missing username")
      (s/blank? (:password target))      (error-message "missing password")
      (not (valid-port? (:port target))) (error-message "missing port"))))

(defn validate-target-list [target-list]
  (reduce validate-target-reducer [] target-list))

(comment
  (validate-target-reducer [] {:host "non" :value "username@non"})
  (validate-target-reducer [] {:host "non" :username "bob" :password "pwd" :port "22" :value "username@non"})
  ;;
  )

(defn run [args]
  (let [parsed-opts                          (parse-opts args cli-options)
        {:keys [port password targets]}      (:options parsed-opts)
        cmd                                  (:arguments parsed-opts)
        target-list                          (parse-target-list targets)]
    (cond
      (help-option? parsed-opts) (println (usage parsed-opts))
      (:errors parsed-opts)      (println (s/join \newline (:errors parsed-opts)))
      (empty? cmd)               (println "missing command")
      (empty? target-list)       (println "missing or invalid target list")
      :else                      (let [final-target-list  (fill-missing-keys target-list password port)
                                       target-list-errors (validate-target-list final-target-list)]
                                   (if-not (empty? target-list-errors)
                                     (doseq [error-message target-list-errors]
                                       (println error-message))
                                     ;;(core/run-cmd-coll final-target-list (first cmd))
                                     final-target-list)))))

(comment
  (run ["--help"])
  (run ["--targets" "user1@host1,user2@host2" "CMD"])
  (run ["--targets" "pwd1:user1@host1,user2@host2" "CMD"])
  (run ["-p" "XX" "--targets" "pwd1:user1@host1,user2@host2" "CMD"])
  (run ["-p" "23" "--targets" "pwd1:user1@host1,user2@host2" "CMD"])
  (run ["--targets" "pwd1:user1@host1:23,user2@host2:24" "CMD"])
  (run ["--password" "PWD" "--targets" "pwd1:user1@host1:23,user2@host2:24" "CMD"])



  (run ["--targets" "meth01@10.18.4.25" "ls"])
  (run ["--targets" "meth01:meth01@10.18.4.25,meth01@10.18.4.25" "ls"])
  (run ["--password" "meth01"  "--targets" "meth01@10.18.4.25" "ls"])



  ;;
  )