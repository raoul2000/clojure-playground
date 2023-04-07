(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh set-refresh-dirs]]
            [myname.myapp :as app]))


(comment
  (set-refresh-dirs "src")
  (refresh)
  
  (app/start)
  
  ;;
  )

