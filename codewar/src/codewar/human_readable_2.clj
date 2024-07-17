(ns codewar.human-readable-2)

;; https://www.codewars.com/kata/52742f58faf5485cae000b9a/train/clojure

(def time-unit {"year"       (* 365 24 3600)
                "day"        (* 24 3600)
                "hour"       3600
                "minute"     60
                "second"     1})

(defn decompose [n]
  (->> (vals time-unit)
       (reduce (fn [{:keys [v r]} u]
                 {:v (rem v u)
                  :r (conj r (quot v u))}) {:v n :r []})
       :r))

(comment
  (decompose 3600)
  (decompose 132030240)
  (decompose (+ (* 24 3600) 121))
  (decompose 0)
  (decompose (+ (* 2 3600) 5)))

(defn add-scale-words [xs]
  (map (fn [v unit-name] [v (str " " unit-name)]) xs (keys time-unit)))

(comment
  (add-scale-words [0 0 0 1 2 3]))

(defn zero-time? [x]
  ((comp zero? first) x))

(comment
  (zero-time? [0 2]))

(defn pluralize [[v unit-name]]
  [v (if (= 1 v) unit-name (str unit-name "s"))])

(comment
  (pluralize [1 "hour"])
  (pluralize [2 "hour"]))


(defn add-separators [xs]
  (if (= 1 (count xs))
    xs
    (cons (drop-last (interleave (drop-last xs) (repeat ", "))) [" and " (last xs)])))

(comment
  (add-separators [[1 "hour"] [23 "minutes"]])
  (add-separators [[5 "days"] [1 "hour"] [23 "minutes"]]))

(defn formatDuration [secs]
  (if (zero? secs)
    "now"
    (->> secs
         decompose
         add-scale-words
         (remove zero-time?)
         (map pluralize)
         add-separators
         flatten
         (apply str))))

(comment
  (formatDuration 3601)
  (formatDuration 2538000))


(comment
  (time (repeatedly 10000 #(formatDuration 2538000)))
  (quot 3662 60)
  (rem 3662 60)
  (let [n 30662]
    (map #(vector (quot n %) (rem n %)) [84600 3600 60 1]))

  (loop [d [84600 3600 60 1]
         s 3662
         r []]
    (if (zero? s)
      r
      (recur
       (rest d)
       (rem s (first d))
       (conj r (quot s (first d))))))

  (let [n 3662
        sec (mod n 60)
        min (mod  (quot (+ n sec) 60))
        hour (mod (+ (quot n 3600)))]
    [min sec])
  ;;
  )
