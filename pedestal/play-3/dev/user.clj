(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh set-refresh-dirs]]
            [play-3.play-3 :as server]))

(def system nil)

;; from https://cognitect.com/blog/2013/06/04/clojure-workflow-reloaded

;; first run start then loop:
;; - update files (e.g. handler)
;; - eval (reset)
(defn start
  []
  (alter-var-root #'system server/start))

(defn stop
  "Shuts down and destroys the current development system."
  []
  (alter-var-root #'system
                  (fn [s] (when s (server/stop s)))))

(defn reset []
  (stop)
  (refresh :after 'user/start))

(comment
  (reset)
  (start)
  (stop)

  (ns-unalias 'user 'server)
  (set-refresh-dirs "src")

  ;;
  )