(ns codewar.mixin
  (:gen-class))

;; https://www.codewars.com/kata/5629db57620258aa9d000014/train/clojure

(def occurence-char-value (comp first second))

(defn pair? [oc]
  (= 2 (count oc)))

(defn lower-case-letter? [c]
  (and (Character/isLetter c)
       (Character/isLowerCase  c)))

(defn remove-occurence-lower-or-equal-to-1 [m]
  (remove #(<= (second %) 1) m))

(defn create-occurence-coll
  "Given string *s* returns a seq of occurences, each one being a vector
   where the first element is the lower case letter char and the second 
   its occurences in *s*.
   
   ```
   (create-occurence-coll \"zerzerzerzer\")
   => ([\\z 4] [\\e 4] [\\r 4])
   ```
   "
  [s]
  (->> s
       (filter lower-case-letter?)
       (frequencies)
       (remove-occurence-lower-or-equal-to-1)))

(defn create-tagged-occurence-coll
  "Given string *s* returns a seq of occurences tagged with *tag*.
   
   tagged occurence shape: `[\"tag\" [\\a 4]]`
   "
  [s tag]
  (map (partial vector tag) (create-occurence-coll s)))

(defn merge-tagged-occurence-pair 
  "Given 2 tagged occurences, returns a tagged occurence where the tag is the one
   of the max char count or is `\"=\"` when equals char count are equals"
  [oc1 oc2]
  (let [[_ [c1 cnt1]] oc1
        [_ [_  cnt2]] oc2]
    (cond
      (= cnt1 cnt2) ["=" [c1 cnt1]]
      (> cnt1 cnt2) oc1
      :else         oc2)))

(defn merge-tagged-occurences [item]
  (if (pair? item)
           ;; item = (["1" [\a 4]] ["2" [\a 4]]) - merging
    (apply merge-tagged-occurence-pair item)
    (first item)))

(defn create-substring [[id [c n]]]
  (str id ":" (apply str (repeat n c))))

(defn sort-substrings [coll]
  (->> coll
       (sort-by count)              ;; sort by substring length
       (partition-by count)         ;; prepare for 2 level sort
       (sort-by (comp count first)) ;; parition order
       (map sort)                   ;; partition items order
       (reverse)
       (flatten)))

(defn mix [s1 s2]
  (let [tagged-occurences (concat (create-tagged-occurence-coll s1 "1")
                                  (create-tagged-occurence-coll s2 "2"))]
    (->> tagged-occurences
         (sort-by      occurence-char-value)
         (partition-by occurence-char-value)
         (map merge-tagged-occurences)
         (map create-substring)
         (sort-substrings)
         (interpose "/")
         (apply str))))
