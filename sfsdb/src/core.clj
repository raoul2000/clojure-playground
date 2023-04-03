(ns core
  (:require [clojure.tools.cli :refer [parse-opts]]
            ))

(def cli-options [["-p" "--port" "port number"
                   :default 8080]])

(comment
  
  (parse-opts ["-h"] cli-options :in-order true)

  )