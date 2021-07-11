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

  ;; => 6 tot = 1
  ;; abcdef

  ;; work on a data structure built as a list of list.
  ;; each list contains all tuple for the nth item of the distance list
  ;; for example, given the distance list [22 45 40 12]
  ;; [
  ;;  [ [22 45] [22 40] [22 12] ] ..... all 2-tuples for 22
  ;;  [ [45 40] [45 12]         ] ..... all 2-tuples for 45
  ;;  [ [40 12]                 ] ..... all 2-tuples for 40
  ;; ]

(defn groups-of-one
  "Returns the initial data structure, a list of list of
  one item groups for each value in the `xs` seq.
  Ex: (groups-of-one [6 3 4]) 
  => [ [[6]] [[3]] [[4]]] "
  [xs]
  (map #(vector (vector %)) xs))

(comment
  (groups-of-one [1 2 3]))

(defn dispatch
  "Returns a new list of groups list where `v` is added to 
  all existing groups in `grps`
  Ex: (dispatch 99  [ [[:a]  [:b]]
                      [[:c]  [:d]] ])
  => ([:a 99] [:b 99] [:c 99] [:d 99])"
  [v grps]
  (->> grps
       (reduce #(into %1 %2) [])
       (map #(conj % v))))

(comment
  (dispatch 99  [[[1]] [[2]]])
  (dispatch 99  [[[:a]  [:b]] [[:c]  [:d]]])
  (dispatch 99  [[[:a]  [:b] [:d]] [[:e]  [:f] [:g]]]))

(defn dispatch-all
  "Dispatch all values from `distances` to groups to get a list
  of new groups with one item more.
  Ex:  (dispatch-all [1 2 3] [[[1]] [[2]] [[3]]])
  => [([2 1] [3 1]) ([3 2])]"
  [distances groups-n]
  (loop [dist   distances
         groups (rest groups-n)
         res    []]
    (if-not (seq groups)
      res
      (recur
       (rest dist)
       (rest groups)
       (conj res (dispatch (first dist)  groups))))))

(comment
  (dispatch-all [1 2 3] [[[1]] [[2]] [[3]]])
  (dispatch-all [1 2 3] (groups-of-one [1 2 3]))

  (dispatch-all [1 2 3] [[[2 1] [3 1]] [[3 2]]])

  (dispatch-all [1 2 3] ['([2 1] [3 1]) '([3 2])]))

(defn groups-of-n
  "Returns a list of all groups of `len` items that can be
  creates with value in `lst`"
  [lst len]
  (->> lst
       groups-of-one
       (iterate (partial dispatch-all lst))
       (take len)
       last
       (mapcat identity)))

(comment
  (count (groups-of-n [1 2 3 4 5 6] 1))
  (count (groups-of-n [1 2 3 4 5 6] 2))
  (count (groups-of-n [1 2 3 4 5 6] 3))
  (count (groups-of-n [1 2 3 4 5 6] 4))
  (count (groups-of-n [1 2 3 4 5 6] 5))
  (count (groups-of-n [1 2 3 4 5 6] 6))
  (count (groups-of-n [1 2 3 4 5 6] 7)))

(defn sum-each-group
  "Create a list containing sums of each groups of `k`
   values taken from `ls` seq"
  [k ls]
  (map #(apply + %) (groups-of-n ls k)))

(defn choose-max-n
  "Returns n from `ls` where n <= `mx` or nil if not found"
  [mx ls]
  (reduce (fn [acc n]
            (if (< acc n mx) n acc))
          0
          ls))

(defn choose-best-sum [t k ls]
  (when-not (>  k (count ls))
    (let [res (choose-max-n (inc t) (sum-each-group k ls))]
      (when (pos? res)
        res))))



(comment

  (choose-best-sum 163 4 [50, 55, 56])
  (choose-best-sum 163 2 [50, 55, 56])
  (choose-best-sum 163 3 [50, 55, 56, 57, 58])
  (choose-best-sum 230 3 [91 74 73 85 73 81 87])

  (->> (groups-of-n [91 74 73 85 73 81 87] 3)
       (map #(apply + %))
       (reduce (fn [acc n] (if (< acc n 231) n acc)) 0))

  (apply max (take-while (partial > (inc 163)) [1 2 3 163 2 168]))
  ;;
  )