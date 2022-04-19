(ns toolbox.depend.save
  (:require [clojure.string :as s]
            [clojure.data.json :as json]))

(def supported-output-formats #{"json" "tgf"})

(defn supported-output-format? [format]
  (contains? supported-output-formats format))

(defn tgf-node-mapper [dep-item]
  (let [node-label (:script-path dep-item)]
    (str (.hashCode node-label)
         " "
         node-label)))

(defn tgf-node-definition
  "given a dep-list, returns a list of string in the form *nodeId script-path*"
  [dep-list]
  (into #{} (map tgf-node-mapper dep-list)))

(defn tgf-edge-definition [dep-list]
  (let [edges (map (fn [{:keys [script-path deps]}]
                     (vector script-path
                             (flatten (->> (map :local-files deps)
                                           (remove nil?))))) dep-list)]
    (reduce (fn [res [start-node end-nodes]]
              (into res (map #(str (.hashCode start-node) " "  (.hashCode %))) end-nodes)) #{} edges)))

(defn format-tgf [deps]
  (let [node-def (tgf-node-definition deps)
        edge-def (tgf-edge-definition deps)]
    (str (s/join "\n" node-def)
         "\n#\n"
         (s/join "\n" edge-def))))

(defn format-json [deps]
  (json/write-str deps))

(defn deps->format [deps format]
  (case format
    "tgf"  (format-tgf  (flatten deps))
    "json" (format-json deps)
    (format-json deps)))

(defn save-graph
  ([output-path deps]
   (save-graph output-path deps "json"))

  ([output-path deps format]
   (when-not (supported-output-format? format)
     (throw (Exception. (str "Unsupported output format: " format))))
   (spit output-path (deps->format deps format))))

(defn save-deps [output-file-path deps format]
  (spit output-file-path (deps->format deps format)))

(comment
  (def dep1 [{:script-path "./resources/test/root/start.bash",
              :deps []}

             {:script-path "./resources/test/root/sub-1/sub-2/purple.bash",
              :deps '({:ref {:match "green.bash", :line-nums #{3}},
                       :local-files '("./resources/test/root/sub-1/sub-2/green.bash"
                                      "./alt/green.bash")})}


             {:script-path "./resources/test/root/sub-1/orange.bash",
              :deps
              '({:ref {:match "sub-2/purple.bash", :line-nums #{3}},
                 :local-files '("./resources/test/root/sub-1/sub-2/purple.bash")})}])

  (def dep2 [{:script-path "./resources/test/root/start_2.bash",
              :deps []}

             {:script-path "./resources/test/root/sub-1/sub-2/purple_2.bash",
              :deps '({:ref {:match "green.bash", :line-nums #{3}},
                       :local-files '("./resources/test/root/sub-1/sub-2/green.bash")})}])

  (def dep3 [{:script-path "./resources/test/root/start.bash",
              :deps []}

             {:script-path "./resources/test/root/sub-1/sub-2/purple_2.bash",
              :deps '({:ref {:match "green.bash", :line-nums #{3}},
                       :local-files '("./resources/test/root/sub-1/sub-2/green.bash")})}])

  (map tgf-node-definition [dep1 dep2 dep3])
  (flatten [1 2])
  (into #{} (tgf-node-definition (flatten [dep1 dep2 dep3])))

  (println (deps->format (flatten [dep1 dep2 dep3]) "tgf"))
  ;;
  )


(comment

  (def deps [{:script-path "./resources/test/root/start.bash",
              :deps
              '({:ref {:match "sub-1/orange.bash" :line-nums #{3}},
                 :local-files '("./resources/test/root/sub-1/orange.bash")}

                {:ref {:match "sub-1/sub-2/purple.bash", :line-nums #{4}},
                 :local-files '("./resources/test/root/sub-1/sub-2/purple.bash")})}


             {:script-path "./resources/test/root/sub-1/sub-2/purple.bash",
              :deps '({:ref {:match "green.bash", :line-nums #{3}},
                       :local-files '("./resources/test/root/sub-1/sub-2/green.bash")})}


             {:script-path "./resources/test/root/sub-1/orange.bash",
              :deps
              '({:ref {:match "sub-2/purple.bash", :line-nums #{3}},
                 :local-files '("./resources/test/root/sub-1/sub-2/purple.bash")})}


             {:script-path "./resources/test/root/sub-1/sub-2/green.bash",
              :deps '({:ref {:match ".bash_profile", :line-nums #{2}},
                       :local-files nil})}])

  (json/write-str deps)
  (save-graph "./test/output/out.json" deps)
  (save-graph "./test/output/out.tgf" deps "tgf")
  (save-graph *out* deps "tgf")


  ;;
  )

