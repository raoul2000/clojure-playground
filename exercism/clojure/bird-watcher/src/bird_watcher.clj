(ns bird-watcher)

(def last-week [0 2 5 3 7 8 4])
(def birds-per-day [2 5 0 7 4 1])

(defn today [birds]
  (last birds))

(defn inc-bird [birds]
  (update birds (dec (count birds)) inc))

(defn day-without-birds? [birds]
  (boolean (some zero? birds)))

(defn n-days-count [birds n]
  (apply + (take n birds)))

(defn busy-days [birds]
  (count (filter #(> % 4) birds)))

(defn odd-week? [birds]
  (every?  #(= '(1 0) %) (partition 2 birds)))
