(ns src.core
  (:require [clj-ssh.ssh :as ssh]
            [clojure.string :as s]))

(def ip "10.18.4.25")
(def username "meth01")
(def password "meth01")
(def session-opts {:strict-host-key-checking :no
                   :username username
                   :password password})
;; function below opens an ssh session, submit the shell command, get the result
;; and then closes the session.
(defn run-cmd [cmd]
  (let [agent (ssh/ssh-agent {})]
    (let [session (ssh/session agent ip {:strict-host-key-checking :no
                                         :username username
                                         :password password})]
      (ssh/with-connection session
        (ssh/ssh session {:in cmd})))))

(comment
  (run-cmd "ls -rtl")
  ;; one shot
  (let [agent (ssh/ssh-agent {})]
    (let [session (ssh/session agent ip {:strict-host-key-checking :no
                                         :username username
                                         :password password})]
      (ssh/with-connection session
        (let [result (ssh/ssh session {:in "echo hello"})]
          (println (result :out))
          (prn result)))))

  ;;
  )

;; what we would like to do now is to open an ssh session and keep it open 
;; submit ssh command to the connected shell and on demand, close the session

(comment
  ;; simply by NOT using ssh/with-connection, the session can be used
  ;; and when done, it must be closed manually

  (def session (ssh/session (ssh/ssh-agent {}) ip session-opts))
  (ssh/ssh session {:in "ps"})
  (ssh/connected? session)
  (ssh/disconnect session)

  ;;
  )

(def cur-session (atom {}))

(comment
  ;; same as above but using atom to store the current session
  ;; and smart process command output

  (swap! cur-session (constantly 1))

  (swap! cur-session (fn [old]
                       (when (instance? com.jcraft.jsch.Session old)
                         (print "trying to disconnect session")
                         (ssh/disconnect old))
                       (ssh/session (ssh/ssh-agent {}) ip session-opts)))

  (ssh/ssh @cur-session {:in "whoami"})
  (def out (:out (ssh/ssh @cur-session {:in "echo \"begin====\"; ls ; echo \"end====\""})))
  (rest (take-while #(not= % "end====") (drop-while #(not= % "begin====") (s/split out #"\r\n"))))

  (ssh/disconnect @cur-session)

  ;;
  )

(comment
  ;; is it possible to send a command like "tail -F ./some/file.txt" ?
  ;; Should a output stream be used in this case ?

  (def session (ssh/session (ssh/ssh-agent {}) ip session-opts))
  (ssh/ssh session {:in "ls"})

  (let [{:keys [channel out-stream]} (ssh/ssh-shell session "top" :stream {})]
    (while (ssh/connected-channel? channel)
      (println "waiting ...")
      (let [bytes (byte-array 1024)
            n (.read out-stream bytes 0 1024)]
        (print (String. bytes 0 n)))
      (Thread/sleep 100))
    #_(let [bytes (byte-array 1024)
            n (.read out-stream bytes 0 1024)]
        (print (String. bytes 0 n))))

  (ssh/connected? session)
  (ssh/disconnect session)

  (Thread )

  ;;
  )

