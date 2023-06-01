(ns dev.user
  (:require
   [portal.api :as p]))

;;(def portal (p/open))
(def p (p/open {:launcher :vs-code}))

(add-tap #'p/submit)

(tap> :hello) ; Start tapping out values

;; TODO: setup morse https://github.com/nubank/morse/blob/main/docs/guide.adoc

; Syntax error (UnsupportedClassVersionError) compiling new at (dev\nu\morse.clj:83:3).
; javafx/embed/swing/JFXPanel has been compiled by a more recent version of the Java Runtime (class file version 55.0), this version of the Java Runtime only recognizes class file versions up to 52.0
