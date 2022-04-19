(ns toolbox.depend.file-match
  (:require [clojure.string :as s]))


(defn split-path-by-slash
  "split string *s* by sperator '/' and returns the tokens list"
  [s]
  (remove empty? (s/split s #"/")))

(defn common-seg [p1 p2]
  (let [seg1 (reverse (split-path-by-slash p1))
        seg2 (reverse (split-path-by-slash p2))]
    (->> (map #(when (= %1 %2) %1) seg1 seg2)
         (take-while (comp not nil?))
         reverse)))

(defn path-match-score [path path-list]
  (let [score (->> path-list
                   (map #(vector (count (common-seg path %)) %))
                   (reduce (fn [res [score path]]
                             (update res score #(conj % path))) {})
                   (remove (comp zero? first))
                   (into {}))]
    (when (pos-int? (count score))
      score)))

(defn best-path-match [path path-list]
  (when-let [score (path-match-score path path-list)]
    (val (apply max-key key score))))
