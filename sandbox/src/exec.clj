(ns exec
  (:require [clojure.java.shell :as shell]
            [babashka.fs :as fs]))

(comment

  (def cwd (str (fs/cwd)))

  (shell/sh  "PowerShell.exe" "dir" "." :dir "c:/tmp")
  (shell/sh  "cmd.exe" "/c" "dir" "." :dir "c:/tmp")
  (shell/sh  "bash.exe" "ls" "." :dir "c:/tmp")


  (shell/sh "notepad.exe" (str (fs/path cwd "README.md")))
  (shell/sh "notepad.exe" (str (fs/path cwd "CHANGELOG.md")))
  (shell/sh  "explorer" "c:\\tmp")
  (shell/sh  "explorer" cwd)



  ;; while a command is executed (i.e until the launched program exit) no other
  ;; instance of the same program can be launched:

  (do
    (shell/sh "notepad.exe" (str (fs/path cwd "CHANGELOG.md")))
    (shell/sh "notepad.exe" (str (fs/path cwd "README.md"))))

  ;; let's try with future
  (dotimes [n 10]
    (future (shell/sh "notepad.exe" (str (fs/path cwd "README.md"))))
    (future (shell/sh "notepad.exe" (str (fs/path cwd "CHANGELOG.md")))))

  (future (shell/sh  "putty"  "-ssh" "user.name@meth01@dtvadl-meth01.awseuw3.em-dtv.int@jump"))

  ;; how to handle errors
  (def cmd-ok (future
                (try
                  (shell/sh "notepad++.exe" (str (fs/path cwd "README.md")))

                  ;; for this one, the full path is required as it is not in PATH (like notepad.exe)
                  #_(shell/sh "C:\\Program Files\\Notepad++\\notepad++.exe" (str (fs/path cwd "README.md")))
                  (catch Exception e {:sh-error  (.getMessage e)}))))
  (realized? cmd-ok)
  @cmd-ok
  ;; when the sh command could be executed : {:exit 0, :out "", :err ""}
  ;; otherwise {:sh-error "Cannot run program \"notepad++.exe\": CreateProcess error=2, Le fichier spécifié est introuvable"}

  ;; we must ensure that it doesn't consume too many resources (thread ?)

  ;;
  )
