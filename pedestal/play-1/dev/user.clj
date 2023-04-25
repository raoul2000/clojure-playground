(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh set-refresh-dirs ]]
            [myservice.api :as server]))




(comment
  (set-refresh-dirs "src")

  (def srv (server/start-dev))

  (server/stop-dev srv)
  (refresh)

  (server/stop-dev)


  ;;
  )

