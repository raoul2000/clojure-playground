(ns codewar.sumofk-2)


  ;; [a b c d e f] 
  ;; => 1 tot = 6
  ;; a
  ;; b
  ;; c
  ;; d
  ;; e
  ;; f

  ;; => 2 tot = 15
  ;; ab ac ad ae af 
  ;; bc bd be bf
  ;; cd ce cf
  ;; de df
  ;; ef

  ;; => 3 tot = 20
  ;; abc abd abe abf acd ace acf ade adf aef
  ;; bcd bce bcf bde bdf bef 
  ;; cde cdf cef
  ;; def

  ;; => 4 tot = 15
  ;; abcd abce abcf abde abdf abef acde acdf acef adef
  ;; bcde bcdf bcef bdef
  ;; cdef

  ;; => 5 tot = 6
  ;; abcde abcdf abcef abdef acdef
  ;; bcdef

(defn init-list [xs]
  (map #(vector (vector %)) xs))

(comment
  (init-list [1 2 3]))

(defn dispatch [k ll]
  (->> ll
       (reduce #(into %1 %2) [])
       (map #(conj % k))))

(comment
  (dispatch 99  [[[1]] [[2]]])
  (dispatch 99  [[[:a]  [:b]] [[:C]  [:d]]])
  (dispatch 99  [[[:a]  [:b] [:d]] [[:e]  [:f] [:g]]]))

(defn dispatch-all [xs dst]
  (loop [x xs
         d dst
         res []]
    (if (empty? (rest d))
      res
      (recur
       (rest x)
       (rest d)
       (conj res (dispatch (first x) (rest d)))))))

(comment
  (dispatch-all [1 2 3] [[[1]] [[2]] [[3]]])
  (dispatch-all [1 2 3] (init-list [1 2 3]))
  (dispatch-all [1 2 3] [[[2 1] [3 1]] [[3 2]]])
  (dispatch-all [1 2 3] ['([2 1] [3 1]) '([3 2])]))

(defn all-sub-lst [lst len]
  (->> lst
       init-list
       (iterate (partial dispatch-all lst))
       (take len)
       last
       (mapcat identity)))

(comment
  (count (all-sub-lst [1 2 3 4 5 6] 1))
  (count (all-sub-lst [1 2 3 4 5 6] 2))
  (count (all-sub-lst [1 2 3 4 5 6] 3))
  (count (all-sub-lst [1 2 3 4 5 6] 4))
  (count (all-sub-lst [1 2 3 4 5 6] 5)))

(defn compute-sum [k ls]
  (->>  k
        (all-sub-lst ls)
        (map #(apply + %))))

(defn choose-max-n [mx ls]
  (reduce (fn [acc n]
            (if (< acc n mx) n acc))
          0
          ls))

(defn choose-best-sum-ok [t k ls]
  (when-not (>  k (count ls))
    (let [res (choose-max-n (inc t) (compute-sum k ls))]
      (when (pos? res)
        res))))

(defn choose-best-sum [t k ls]
  (let [res (choose-best-sum-ok t k ls)]
    (prn ls)
    res))

(comment
  (choose-best-sum-ok 331 5 [91 74 73 85 73 81 87])
  (choose-best-sum 163 4 [50, 55, 56])
  (choose-best-sum 163 2 [50, 55, 56])
  (choose-best-sum 163 3 [50, 55, 56, 57, 58])
  (choose-best-sum 230 3 [91 74 73 85 73 81 87])

  (->> (all-sub-lst [91 74 73 85 73 81 87] 3)
       (map #(apply + %))
       (reduce (fn [acc n] (if (< acc n 231) n acc)) 0))

  (apply max (take-while (partial > (inc 163)) [1 2 3 163 2 168]))
  ;;
  )