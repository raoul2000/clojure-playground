(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh set-refresh-dirs ]]
            [myservice.api :as server]))




(comment
  (set-refresh-dirs "src")

  (def srv (server/start-dev))
;; #function[io.pedestal.interceptor/eval7439/fn--7440/fn--7441],
  
  
  (server/stop-dev srv)
  (refresh)

  (server/stop-dev)


  ;;
  )

