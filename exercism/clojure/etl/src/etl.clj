(ns etl
  (:require [clojure.string :refer [lower-case]]))


(defn etl-reducer [m [k vs]]
  (into m (for [val vs]
            [(lower-case val) k])))


(defn transform [source]
  (reduce etl-reducer {} source))


(comment

  (transform {1 ["APPLE" "ARTICHOKE"], 2 ["BOAT" "BALLERINA"]})

  ;;
  )
