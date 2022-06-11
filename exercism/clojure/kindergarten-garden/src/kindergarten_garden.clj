(ns kindergarten-garden
  (:require [clojure.string :refer [split lower-case]]))

;; [:a :b :c :d 1 2 3 4]  --> [ [:a :b 1 2] [:c :d 3 4]] --> {:alice [:a :b 1 2]
;;                                                            :bob   [:c :d 3 4]}

(def class-kids ["alice" "bob" "charlie" "david" "eve" "fred" "ginny" "harriet" "ileana" "joseph" "kincaid" "larry"])
(def plant-name {\V :violets, \R :radishes, \C :clover, \G :grass})

(defn code-row->plant-name 
  "Converts a list of plants code into a list of plants keyword.
   (example: [\"VC\"] --> [:violets :clover])"
  [^String code]
  (mapv plant-name code))

(def keywordize (comp keyword lower-case))

(defn garden-1
  ([plants]
   (garden-1 plants class-kids))
  ([plants kids]
   (let [kid-list (map keywordize (sort kids))]
     (->> (split plants #"\n")
          (map code-row->plant-name)
          (map (partial partition 2))
          (#(map concat (first %) (last %)))
          (interleave kid-list)
          (apply hash-map)))))

;; improve solution : use zipmap

(defn garden
  ([plant-cups]
   (garden plant-cups class-kids))
  ([plant-cups kids]
   (let [kid-list (mapv keywordize (sort kids))]
     (->> (split plant-cups #"\n")
          (map code-row->plant-name)
          (map (partial partition 2))
          (#(map concat (first %) (last %)))
          (zipmap kid-list)))))


