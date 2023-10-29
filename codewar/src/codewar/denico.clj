(ns codewar.denico)

;; https://www.codewars.com/kata/596f610441372ee0de00006e/train/clojure



;; We start with a string to decode.
;; First we must store this string in a grid like shown below
;; 1 2 3 4 5
;; ---------
;; c s e e r
;; n t i o f
;; a r m i t
;;   o n   
;;
;; and then, re order each column following the numeric key 
;; computed before. For example, il this key is (2 3 1 5 4)
;; we must set column 2 to first position
;; ... then column 3 to be in second position
;; ... then column 1 to be in third position
;; ... etc
;; When done, we have a new grid like below :
;; 2 3 1 5 4
;; ---------
;; s e c r e
;; t i n f o
;; r m a t i
;; o n
;; Last is to convert this grid into a string
;;

;; the numeric key is created from a string key

(defn create-numeric-key [k]
  (let [sorted-letters (mapv identity (sort k))
        letter-pos-map (reduce-kv (fn [m idx letter]
                                    (assoc m letter idx))
                                  {} sorted-letters)]
    (map #(get letter-pos-map %) k)))

(comment
  (create-numeric-key "crazy")
  ;; => (1 2 0 4 3)

  ;;
  )


(comment
  (def k "crazy")
  (def m "cseerntiofarmit on  ")

;; to store the grid we'll be using a vector where each item is a column, 
;; and a column is a list of letters.
;; The number of column is the size of the key

  (def col-count (count k))

  ;; the number of lines is length of msg modulo col-count
  (def line-count (quot (count m) col-count))

  ;; using 'partition' we can get all lines
  (def lines (partition col-count col-count m))

  ;; we will assume that length of message to decode is multiple of col-count, so no
  ;; extra padding is required for 'partition'

  ;; ok we have lines, but we want to store column, because the columns will be
  ;; re-ordered following num-key, not to lines.

  ;; We want to turn a list of lines :
  ;; ( (:a :b :c) (:d :e :f) (:g :h :i))
  ;; into a lit of columns
  ;; ( (:a :d :g) (:b :e :h) (:c :f :i))

  (partition 1 col-count m)
  (partition 1 col-count (rest m))
  (partition 1 col-count (rest (rest m)))
  (partition 1 col-count (rest (rest (rest m))))
  (partition 1 col-count (rest (rest (rest (rest m)))))

  (defn msg->cols [m]
    (->> (iterate rest m)
         (take col-count)
         (map #(partition 1 col-count %))
         (mapv flatten)))

  (def cols (msg->cols "cseerntiofarmit on  "))

  ;; we have now a vector where each item is a column of letters
  ;; We must re-order these column following the num-key previously computed

  (def num-key '(2 3 1 5 4))

  ;; the col 2 (index = 1) should come first
  ;; ... then the col (index = 2) should follow
  ;; etc...

  (def new-order-cols (reduce (fn [acc pos]
                                (conj acc (get cols (dec pos))) ;; dec because num-key is 1 based index (and not zero)
                                )[] num-key))
  new-order-cols

  ;; Now we have a list of re-ordered cols and we want to turn them
  ;; back into a list of lines

  ;; let's see what a grid looks like when turned into a string
  (defn grid->str [v]
    (apply str (flatten v)))
  
  (grid->str new-order-cols)


  (->> (grid->str new-order-cols)
       (msg->cols)
       #_(grid->str)
       )
  ;;
  )

(defn str->grid [col-count s])

(comment

  (def s1 (partition 5 5 "cseerntiofarmit on  "))

  (loop [parts s1
         grid (take (count (first s1)) (repeat []))]
    (if (empty? (first parts))
      grid
      (recur (map rest s1)
             ())))

  ;;
  )
(defn denico [k message]
  (if (= 1 (count k))
    m
    (let [nkey (create-numeric-key k)]
      (->> message
           (str->to-grid (count nkey))
           (re-order nkey)
           (grid->str (count nkey))))))

(comment

  (denico "crazy" "cseerntiofarmit on  ")
  ;;
  )