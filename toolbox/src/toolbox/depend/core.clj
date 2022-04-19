(ns toolbox.depend.core
  (:require [toolbox.depend.file-tree :as ft]
            [toolbox.depend.analyze :as a]
            [toolbox.depend.file-match :as fm]))


(defn script-deps-children [script-path all-files]
  {:script-path script-path
   :deps        (->> (a/logfile-mapper script-path)
                     :result
                     (map (fn [[script line-nums]]
                            {:ref        {:match     script
                                          :line-nums line-nums}
                             :local-files (fm/best-path-match script all-files)})))})

(defn deps->local-files [deps]
  (into #{} (mapcat :local-files (:deps deps))))

(defn create-deps-tree [script-path root-path]
  (let [all-files (ft/list-all-files root-path)]
    (loop [current    script-path
           to-process #{}
           processed  #{}
           result     []]
      (if-not current
        result
        (let [dep-map        (script-deps-children current all-files)
              dep-files      (deps->local-files dep-map)
              new-processed  (conj processed current)
              new-to-process (into to-process (remove new-processed dep-files))]
          (recur (first new-to-process)
                 (into #{} (rest new-to-process))
                 new-processed
                 (conj result dep-map)))))))





