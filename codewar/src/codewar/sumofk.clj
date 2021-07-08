(ns codewar.sumofk)

(defn som [l] (reduce + l))


(defn products
  "add value v at the begining of each seq items in xs
   hence creating a new sequence that is returned"
  [v xs]
  (map (partial cons v) xs))

(comment
  (products 1 [[2 3] [3]]))

(defn all-products
  " ex: (all-products 2 [1 2 3 4]) => "
  [v xs]
  (let [seq-len (count (first xs))]
    (loop [v1 v
           l1 xs
           res []]
      (if (empty? l1)
        res
        (recur
         (rest v1)
         (drop seq-len l1)
         (cons (products (first v1) l1) res))))))

(comment
  (drop 2 [1 2 3])
  (all-products [1 2 3] [[1] [2] [3]])
  (products 1 [[2] [3]])
  (products 2 [[3]])

  (all-products [1 2 3] [[1 2] [1 3] [2 3]])
  (all-products [1 2 3 4] [[1 2] [1 3] [1 4] [2 3] [2 4]])
  (products 1 [[2 3]]))
(defn combine-1 [xs m]
  (assoc m 1 (map vector xs)))

(comment
  (combine-1 [1 2 3] {}))

(defn combine-all [xs m]
  (assoc m (count xs) xs))

(comment
  (combine-all [1 2 3] {}))


{1 [[50] [55] [45]]
 2 [[50 55] [50 45] [55 45]]
 3 [[50 55 45]]}

(defn sub-lst [v lst len]
  (if (< len 2)
    (throw (IllegalArgumentException. "v must be greter than 1"))
    (loop [xs lst
           res []]
      (if (< (count xs) (dec len))
        res
        (recur
         (rest xs)
         (conj res (into [v] (take (dec len) xs))))))))

(comment
  (sub-lst 1 [2 3 4] 2)
  (sub-lst 2 [3 4] 2)
  (sub-lst 3 [4] 2)
  (sub-lst :a [:b :c :d :e] 2)
  (sub-lst 3 [:a :b :c :d :e] 3)

  (sub-lst 3 [4] 4)

  (sub-lst 1 [2 3 4 5 6] 3)
  (sub-lst 2 [3 4 5 6] 3)
  (sub-lst 3 [4 5 6] 3)
  ;; etc ...
  (loop [l [1 2 3 4 5]
         res []]
    (if (empty? l)
      res
      (recur
       (rest l)
       (into res (sub-lst (first l) (rest l) 2))))))

(defn all-sub-lst [lst len]
  (if (= 1 len)
    (map vector lst)
    (loop [l lst
           res []]
      (if (empty? l)
        res
        (recur
         (rest l)
         (into res (sub-lst (first l) (rest l) len)))))))

(comment
  (all-sub-lst [1 2 3 4 5 6] 3)
  (all-sub-lst  [91 74 73 85 73 81 87] 4))

(defn compute-weight [lst len]
  (map #(apply + %) (all-sub-lst lst len)))

(comment
  (compute-weight [91, 74, 73, 85, 73, 81, 87] 3)
  (compute-weight [91 74 73 85 73 81 87] 4)

  (doseq [route (all-sub-lst  [91 74 73 85 73 81 87] 4)]
    (printf "%s = %d\n" route (apply + route)))
  ;;
  )

(defn choose-best-sum [t k ls]
  (when-not (< (count ls) k)
    (let [dist (remove #(> % t) (compute-weight ls k))]
      (when-not (empty? dist) (apply max dist)))))

(comment
  (choose-best-sum 230 3 [91, 74, 73, 85, 73, 81, 87])
  (choose-best-sum 10 3 [91, 74, 73])
  (choose-best-sum 163 3 [50 55 56 57 58])
  (choose-best-sum 331 4 [91 74 73 85 73 81 87])
  (choose-best-sum 230 4 [100 76 56 44 89 73 68 56 64 123 2333 144 50 132 123 34 89])


  ;;
  )

(comment
  ;;(choose-best-sum 163, 3, [50, 55, 56, 57, 58])

  ;; abcde
  ;; => 1
  ;; a b c d e 
  ;; => 2
  ;; ab   ac    ad    ae   bc   bd    be   cd   ce   de
  ;; => 3
  ;;                      abc  abd   abe  acd  ace  ade
  ;;                                      bcd  bce  bde
  ;;                                                cde
  ;; => 4
  ;;                                     abcd abce abde acde
  ;;                                               bcde
  ;; => 5
 ;;  abcde  

  (let [v 1
        len 2] ;; must be > 1
    (loop [xs [2 3 4 5]
           res []]
      (if (< (count xs) (dec len))
        res
        (recur
         (rest xs)
         (conj res (into [v] (take (dec len) xs)))))))

  ;;

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
  [[[:a]] [[:b]] [[:c]] [[:d]] [[:e]] [[:f]]]
  [[[:a :b] [:a :c] [:a :d]]]

  (let [lst [1 2 3 4 5 6]]
    (map list lst))

  (map #(conj % 1) (rest '(((1)) ((2)) ((3)))))
  (map #(conj % 2) (rest (rest '((1) (2) (3)))))

  (defn dispatch
    "add v to all lists in coll, where coll is a 
     list of list"
    [v coll]
    (map #(conj % v) coll))
  (dispatch 1 '((2 3) (4 5)))
  (dispatch 1 '((2)))

  (def l '(((2 3) (4 5)) ((6 7) (8 9))))

  (map #(dispatch 1 %) l)

  (defn init-list [xs]
    (map (comp list list) xs))
  (init-list [1 2 3])

  (map #(dispatch 1 %) (rest '(((1 2) (1 3)) ((2 3)))))
  (map #(dispatch 1 %) '((()) ((2 3))))


  (map (fn [m] (map #(conj % 1) m)) (rest '(((1 2) (1 3)) ((2 3)))))
  
{11 [[11]]
 2 [[2]]
 6 [[6]]}
  
{11 [[11 2] [11 6]]
 2 [[2 6]]
 6 [[]]}
  
{11 [[11 2 6] ]
 2 [[]]
 6 [[]]}

  (map vector [1 2] [:a :b])
  (map vector [1 2] [:b :a])

  (map vector [1 2 3 4] '([1 :a] [2 :b] [3 :c]))
  (repeat 3 (rest (take 4 (cycle [:a :b :c :d]))))


  ;;
  )
