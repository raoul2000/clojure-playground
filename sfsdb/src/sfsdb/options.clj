(ns sfsdb.options
  (:require [babashka.fs :as fs]))

(def default {:root-path          (fs/cwd)
              :metadata-extension "meta"})

(defn f[]
  true)

