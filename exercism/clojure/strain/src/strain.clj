(ns strain)

(defn retain [pred? coll]
  (for [a coll
        :when (pred? a)]
    a))

(defn discard [pred? coll]
  (retain (complement pred?) coll))
