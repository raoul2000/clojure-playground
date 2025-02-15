
(ns dev.user
  (:require [portal.api :as p]
            [clojure.tools.namespace.repl :refer (refresh refresh-all set-refresh-dirs)]
            [integrant.core :as ig]
            [server.system :as sys]))


(comment

  ;; portal setup ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

  (def p (p/open {:launcher :vs-code}))
  (add-tap #'p/submit)

  (tap> "hello")
  (p/clear)

  ;; system ctrl ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

  ;; start the system 

  (def system (ig/init sys/config))

  ;; stop the system
  (ig/halt! system)
  

  ;; tools.namespace ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

  ;; see https://github.com/clojure/tools.namespace/
  ;; see https://cognitect.com/blog/2013/06/04/clojure-workflow-reloaded

  ;; NOTE : set-refresh-dirs if needed
  (refresh)
  (refresh-all)
  ;;
  )