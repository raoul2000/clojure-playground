(ns codewar.denico
  (:require [clojure.string :as s]))

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
  (def k "ba")
  (def m "2143658709")

  ;; To store the grid we'll be using a vector where each item is a 
  ;; column (seq of letters). 
  ;; There is as many columns as characters in the key 

  (def col-count (count k))

  ;; using 'partition' we can get the list of lines
  (def lines (partition col-count col-count m))

  ;; we will assume that length of message to decode is multiple of col-count, so no
  ;; extra padding is required for 'partition'

  ;; By applying 'partition' several times on successive rest of m ...

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

  (def cols (msg->cols m))

  ;; we have now a vector where each item is a column of letters
  ;; We must re-order these column following the num-key


  ;; Let's create the num key :

  (def num-key (create-numeric-key k))

  ;; and now re-order the list of columns following numeric key values 
  ;; considered as indexes

  (def new-order-cols (reduce (fn [acc pos]
                                (conj acc (get cols pos))) [] num-key))
  new-order-cols

  ;; Now we have a list of re-ordered cols and we want to turn them
  ;; back into a list of lines and then concat them to get the string

  ;; take the first char of each col and concat them
  (def char-seq (loop [parts new-order-cols
                       result []]
                  (if (empty? (first parts))
                    result
                    (recur (map rest parts)
                           (into result (map first parts))))))

  (s/trim (apply str char-seq))


  ;;
  )

(defn str->grid [col-count s]
  (->> (iterate rest s)
       (take col-count)
       (map #(partition 1 col-count %))
       (mapv flatten)))

(defn re-order [numeric-key cols-grid]
  (reduce (fn [acc pos]
            (conj acc (get cols-grid pos))) [] numeric-key))

(defn grid->str [cols-grid]
  (let [char-seq (loop [parts cols-grid
                        result []]
                   (if (empty? (first parts))
                     result
                     (recur (map rest parts)
                            (into result (map first parts)))))]
    (apply str char-seq)))

(defn denico [k message]
  (if (= 1 (count k))
    message
    (let [nkey (create-numeric-key k)]
      (->> message
           (str->grid (count nkey))
           (re-order nkey)
           (grid->str)
           (s/trim)))))

(comment

  (denico "crazy" "cseerntiofarmit on  ")
  (denico "abc" "abcd")
  (denico "ba" "2143658709")
  (denico "a" "message")
  (denico "key" "eky")
  ;;
  )