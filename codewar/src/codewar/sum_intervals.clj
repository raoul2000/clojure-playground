(ns codewar.sum-intervals)

;; https://www.codewars.com/kata/52b7ed099cdc285c300001cd/train/clojure



  ;; --------min-1------------max-1-------------
  ;; --min-2------------max-2----
(defn partial-overlap? [[min-1 max-1] [min-2 max-2]]
  (<= min-2 min-1 max-2 max-1))

;; -----------min-1------------max-1-------------
;; ----min-2-----------------------------max-2----
(defn complete-overlap? [[min-1 max-1] [min-2 max-2]]
  (<= min-2 min-1 max-1 max-2))

(defn overlap? [i1 i2]
  (or (partial-overlap?  i1 i2)
      (partial-overlap?  i2 i1)
      (complete-overlap? i1 i2)
      (complete-overlap? i2 i1)))

(defn merge-overlaping-intervals [[min-1 max-1] [min-2 max-2]]
  (vector (min min-1 min-2) (max max-1 max-2)))

(defn add-interval [new-interval coll]
  (let [{:keys [result merged]} (reduce (fn [res interval]
                                          (if (and (not (:merged res))
                                                   (overlap? new-interval interval))
                                            (-> res
                                                (assoc :merged true)
                                                (update :result conj (merge-overlaping-intervals new-interval interval)))
                                            (-> res
                                                (update :result conj interval))))
                                        {:result []
                                         :merged false}
                                        coll)]
    {:merged merged
     :result (if merged
               result
               (conj coll new-interval))}))

(defn reduce-interval-coll [coll]
  (loop [intr-coll  coll
         result     []]
    (if (empty? intr-coll)
      result
      (recur (rest intr-coll)
             (:result (add-interval (first intr-coll) result))))))


(defn reduce-overlaping-intervals [c1]
  (loop [coll c1]
    (let [cur (reduce-interval-coll coll)]
      (if (= coll cur)
        coll
        (recur cur)))))


(defn sum-intervals
  [intervals]
  (->> (reduce-overlaping-intervals intervals)
       (map #(- (second %) (first %)))
       (apply +)))

(comment
  (reduce-overlaping-intervals [[1 5]])
  (sum-intervals [[1 5]])
  (sum-intervals [[1 5] [6 10]])
  (sum-intervals [[1 4] [7 10] [3 5]])
  
  (sum-intervals [[-1000000000 1000000000]])
  (sum-intervals [[0 20] [-100000000 10] [30 40]])
  
  ;;
  )

(comment
  (def c1 [[1 7] [10 15]  [6 11]  [2 5] [3 4]])
  (reduce-interval-coll c1)

  (loop [coll c1]
    (let [cur (reduce-interval-coll coll)]
      (if (= coll cur)
        coll
        (recur cur))))

  (add-interval [1 2] [[1 4] [6 8] [10 15]])
  (add-interval [3 7] [[1 4] [6 8] [10 15]])
  (add-interval [4 7] [[1 4] [6 8] [10 15]])
  (add-interval [5 7] [[1 4] [6 8] [10 15]])
  (add-interval [13 17] [[1 4] [6 8] [10 15]])
  (add-interval [17 20] [[1 4] [6 8] [10 15]])
  (add-interval [17 20] [])
  (= [[1 4] [6 8] [10 15]] [[10 15] [1 4] [6 8]])

  (loop [intr-coll  [[1 7] [6 8] [10 15]]
         result     []]
    (if (empty? intr-coll)
      result
      (recur (rest intr-coll)
             (:result (add-interval (first intr-coll) result)))))
  ;;
  )





(comment

  ;; --------min-1------------max-1-------------
  ;; --min-2------------max-2----

  (defn partial-overlap? [[min-1 max-1] [min-2 max-2]]
    (<= min-2 min-1 max-2 max-1))

  (partial-overlap? [1 2] [3 4])
  (partial-overlap? [1 3] [3 4])
  (partial-overlap? [3 4] [1 3])
  (partial-overlap? [1 5] [2 4])

  ;; -----------min-1------------max-1-------------
  ;; ----min-2-----------------------------max-2----
  (defn is-subset? [[min-1 max-1] [min-2 max-2]]
    (<= min-2 min-1 max-1 max-2))

  (defn overlap? [i1 i2]
    (or (partial-overlap? i1 i2)
        (partial-overlap? i2 i1)
        (is-subset? i1 i2)
        (is-subset? i2 i1)))

  (overlap? [1 2] [3 4])
  (overlap? [1 4] [2 4])
  (overlap? [2 4] [1 4])
  (overlap? [2 8] [1 4])
  (overlap? [2 5] [1 8])
  (overlap? [2 5] [10 12])

  (defn merge-overlap-intervals [[min-1 max-1] [min-2 max-2]]
    (vector (min min-1 min-2) (max max-1 max-2)))

  (merge-overlap-intervals [1 5] [2 8])

  (defn add-interval [i coll]
    (map (fn [icoll]
           (if (overlap? i icoll)
             (merge-overlap-intervals i icoll)
             icoll)) coll))


  (add-interval [3 6] [[1 2] [4 8]])
  (add-interval [3 6] [[1 2] [8 9]])



  (defn reduce-interval [coll]
    (loop [i coll
           r []]
      (if (empty? i)
        r
        (let [cur (first i)
              other (rest i)]
          (recur other
                 (cond
                   (empty? r)
                   [cur]

                   (some (partial overlap? cur) r)
                   (add-interval cur r)

                   :else
                   (conj r cur)))))))

  (reduce-interval [[1 2] [3 6] [8 10]])

  (reduce-interval [[1 4] [3 6] [8 10]])

  (reduce-interval [[1 4] [3 6] [8 10]])


  (some (partial overlap? [13 15]) [[1 2] [3 6] [8 10]])
  ;;
  )

