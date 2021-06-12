(ns play
  (:require [clojure.string :as s]))

(defn test1 [isbn]
  (= 0 (mod
        (->> isbn
             reverse
             (filter #(or
                       (Character/isDigit %)
                       (= \X %)))
             (into [])
             (reduce-kv (fn [r k v]
                          (+ r (* (inc k) (if (= \X v)
                                            10
                                            (Character/digit v 10))))) 0)) 11)))

(comment
  (test1 "3-598-21507-X"))

(defn not-dash?
  "return true if char c is not '-'"
  [c]
  (not (= \- c)))

(defn to-int
  "convert char c into integer. x = 10"
  [c]
  (if (= \X c)
    10
    (Character/digit c 10)))

(defn checksum [r k v]
  (+ r (* (inc k) (to-int v))))

(defn test2 [isbn]
  (let [m        (re-matches #"(?:(\d+)\-?)+[X\d]$" isbn)
        no-dash  (filter not-dash? isbn)
        len      (count no-dash)
        valid    (and m (or (= 9 len) (= 10 len)))]
    (if valid
      (= 0 (mod
            (->> no-dash
                 reverse
                 (into [])
                 (reduce-kv checksum 0)) 11))
      false)))

(comment
  (test2 "3-598-2507-9S")
  (test2 "3-598-21507-X")
  (Character/isDigit \1)
  (filter identity "abc")

  (filter #(Character/isDigit %) [\1 \Z])
  ;; ignore non digit chars
  (filter #(not (= \- %)) "1-23X")

  ;; use RE to extract valid chars
  (re-seq #"(\d|X)" "6-232-9X")

  ;; list of int values. X is converted to 10
  (->> (re-seq #"(\d|X)" "6-232-9X")
       (map  second)
       (map #(if (= "X" %)
               10
               (Integer/parseInt %))))
  ;; same as before with one map
  ;; isbn-char->int
  (->> (re-seq #"(\d|X)" "6-232-9X")
       (map #(let [c (second %)]
               (if (= "X" c)
                 10
                 (Integer/parseInt c))))
       (into []))
  ;; but we KNOW that X is only last

  ;; reduce int list using loop : __OK__
  ;; 3598215088 mod 11 === 0
  (loop [isbn (reverse '(3 5 9 8 2 1 5 0 8 8))
         idx 10
         result 0]
    (if (empty? isbn)
      result
      (recur
       (rest isbn)
       (dec idx)
       (+ result (* (first isbn) idx)))))

  ;; (map-indexed vector "foobar")
  (map-indexed vector (reverse '(3 5 9 8 2 1 5 0 8 8)))
  (map-indexed #(* (inc %1) %2) '(1 2))
  (map-indexed #(* (inc %1) %2) (reverse '(3 5 9 8 2 1 5 0 8 8)))

  (apply + (map #(* (inc (first %)) (second %))
                '([0 8] [1 8] [2 0] [3 5] [4 1] [5 2] [6 8] [7 9] [8 5] [9 3])))

  (apply str '(1 2))

  ;; use reduce-kv 
  (->> "3-598-21507-X"
       reverse
       (into [])
       (reduce-kv (fn [r k v]
                    (+ r (* (inc k) (cond
                                      (= \X v) 10
                                      (Character/isDigit v) (Character/digit v 10)
                                      :else 0)))) 0)
       (mod 11))

  (map-indexed vector (reverse '(3 5 9 8 2 1 5 0 8 8)))

  (for [c "1-23-8"
        :when (or
               (Character/isDigit c)
               (= \X c))]
    c)

  (loop [isbn (seq "6-232-9X")
         idx 10
         acc 0]
    (let [c (first isbn)]
      (cond
        (Character/isDigit c)    (recur (rest isbn) (dec idx) (+ acc (* idx (Character/digit c 10))))
        (and (= \X c) (= 1 idx)) (recur (rest isbn) (dec idx) (+ acc (* idx 10)))))))
;; ==============================

(defn isbn-char->int
  [c]
  (if (= \X c)
    10
    (Character/digit c 10)))

(defn isbn? [isbn]
  (let [no-dash (re-matches #"^\d{9}[X\d]$" (s/replace isbn "-" ""))]
    (and
     (some? no-dash)
     (->> no-dash
          (map isbn-char->int)
          (map * (range 10 0 -1))
          (apply +)
          (#(mod % 11))
          zero?))))

(comment
  (apply + (map * [1 2 3] (range 10 0 -1)))
  (apply + (map str "123" (range 10 0 -1)))
  (filter #(if (Character/isDigit %)))
  (isbn? "3-598-21507-X")
  (isbn? "3-598-21515-X")

  ;;
  )