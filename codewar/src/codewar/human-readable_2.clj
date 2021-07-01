(ns human-readable-2)

;; https://www.codewars.com/kata/52742f58faf5485cae000b9a/train/clojure

(def unit {"year"    30456000
           "month"   2538000
           "day"     84600
           "hour"    3600
           "minute"  60
           "second"  1})

(defn spread-1 [n]
  (loop [d (vals unit)
         s n
         r []]
    (if (empty? d)
      r
      (recur
       (rest d)
       (rem s (first d))
       (conj r (quot s (first d)))))))

(defn spread [n]
  (->> (vals unit)
       (reduce (fn [{:keys [v r]} u]
                 {:v (rem v u)
                  :r (conj r (quot v u))}) {:v n :r []})
       :r))


(comment
  (reduce (fn [acc u] {:v (rem (:v acc) u)
                       :r (conj (:r acc) (quot (:v acc) u))}) {:v 3600 :r []} (vals unit))

  (reduce (fn [{:keys [v r]} u]
            {:v (rem v u)
             :r (conj r (quot v u))}) {:v 3600 :r []} (vals unit))


  (spread 3600)
  (spread (+ (* 2 3600) 5)))

(defn add-scale-words [xs]
  (map vector xs (keys unit)))

(comment
  (add-scale-words [0 0 0 1 2 3]))

(defn remove-0 [xs]
  (remove (comp zero? first) xs))

(comment
  (remove-0 [[1 1] [0 2]]))

(defn pluralize [xs]
  (map (fn [[n s]] [n (if (= 1 n) s (str s "s") )]) xs))
(comment
  (pluralize [[1 "hour"] [3 "minute"]]))

(defn add-and [xs]
  (let [u (last (last xs))]
    (if (= "sec" (subs u 0 3))
      (concat (drop-last xs) ["and"] [(last xs)])
      xs)))

(comment
  (add-and [[1 "ee"] [2 "minute"]])
  (add-and [[1 "ee"] [2 "second"]])
  (add-and [[1 "ee"] [1 "second"]])
  )

(defn formatDuration [secs]
  (if (zero? secs)
    "now"
    (->> secs
         spread
         add-scale-words
         remove-0
         pluralize
         add-and)))

(comment
  (formatDuration 3601)
  (formatDuration 2538000)
  )


(comment
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
