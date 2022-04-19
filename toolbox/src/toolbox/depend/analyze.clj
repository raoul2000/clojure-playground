(ns toolbox.depend.analyze
  (:require  [clojure.java.io :as io]))

(def re-bash-script #"(?:/?[\w\d-_{}$]+/)*[\w\d-_]*\.bash\.?[\w\d-_]*")
(defn commented-line? [s] (re-matches #"^\s*#.*" s))

(defn re-match-reducer [result s]
  (-> result
      (update :line-count inc)
      (update :result #(if (commented-line? s)
                         %
                         (if-let [match (re-seq re-bash-script s)]
                           (conj % {:line-num (inc (:line-count result))
                                    :match  match})
                           %)))))

(defn index-by-script [m]
  (reduce (fn [result entry]
            (loop [[script &  remaining] (:match entry)
                   m result]
              (if-not script
                m
                (recur remaining
                       (update m script #(conj (or % (hash-set)) (:line-num entry))))))) {} m))


(defn logfile-mapper
  "Returns a map describing analyze result of *file* script.
   
   ```
   {:line-count 292,
    :result { \"folder/folder/script-1.bash\" #{2 4}
              \"folder/script-2.bash\"        #{214}
    :file \"file path\"}
   ```
   "
  [file]
  (-> (with-open [rdr (io/reader file)]
        (doall
         (reduce re-match-reducer  {:line-count 0
                                    :result     []} (line-seq rdr))))
      (assoc :file (.toString file))
      (update :result index-by-script)))

(comment
  (logfile-mapper "./test/fixture/root-1/start.bash")
  (logfile-mapper "./test/fixture/root-1/other-1.bash"))
