(ns toolbox.dispatch-cmd.core
  (:require [clj-ssh.ssh :as ssh]))


(defn run-remote-cmd
  "Opens an SSH connexion to *hostname* using *options* map settings and then execute
   *scripts* on the remote host. Returns a map desribing script result.

   - `:exit` : script exit code
   - `:out` : script output
   - `:err` : in case of error, stderr
   "
  ([options script host label]
   (let [agent (ssh/ssh-agent {})]
     (let [session (ssh/session agent host options)]
       (ssh/with-connection session
         {:label   label
          :host    host
          :script  script
          :result  (ssh/ssh session
                            {:cmd script})}))))
  ([options script host]
   (run-remote-cmd options script host "")))

(defn create-future-cmd-result [cmd {:keys [host username password label port]}]
  (future (run-remote-cmd {:strict-host-key-checking :no
                           :username                 username
                           :password                 password
                           :port                     (Integer/parseInt port)}
                          cmd
                          host
                          (or label
                              (str username "@" host)))))

(defn run-cmd-coll
  ([target-list cmd]
   (let [future-out (doall
                     (map (partial create-future-cmd-result cmd) target-list))]
     (map deref future-out))))

(comment
  

  ;;
  )


