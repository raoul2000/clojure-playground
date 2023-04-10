(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh set-refresh-dirs]]
            [play-3.play-3 :as server]))


(comment

  (set-refresh-dirs "src")

  (def srv (server/start))
  (server/stop srv)

  (refresh)


  ;;
  )