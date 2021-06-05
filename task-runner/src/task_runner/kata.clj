(ns task-runner.kata
  (:require [clojure.string :as s]))

;; ===================================================
(defn encode-dups [text]
  (let [norm-s (s/upper-case text)
        freq   (frequencies norm-s)]
    (apply str (for [car norm-s]
                 (if (= 1 (freq car))
                   "("
                   ")")))))

(comment
  (encode-dups "Success")
  (encode-dups "(( @")
  ;;
  )

;; ===================================================

(defn longest [s1 s2]
  (s/join (into (sorted-set) (str s1 s2))))

(comment
  (= "aehrsty" (longest "aretheyhere" "yestheyarehere"))
  (= "abcdefghilnoprstu" (longest "loopingisfunbutdangerous" "lessdangerousthancoding"))
  ;;
  )

;; ===================================================

(def opposite-dir {"NORTH" "SOUTH"
                   "WEST" "EAST"})

(defn opposite-dir? [d1 d2]
  (if (or (nil? d1) (nil? d2))
    false
    (or
     (= d2 (opposite-dir d1))
     (= d1 (opposite-dir d2)))))

(comment
  (opposite-dir? "NORTH" "SOUTH")
  (opposite-dir? "NORTH" nil)
  (opposite-dir?  nil "NORTH")
  (opposite-dir?  nil "SOUTH")
  ;;
  )

(defn dirReduc
  [arr]
  (not-empty (reduce #(if (opposite-dir? (peek %1) %2)
                        (pop %1)
                        (conj %1 %2)) [] arr)))

(comment
  (dirReduc ["NORTH", "SOUTH", "SOUTH", "EAST", "WEST", "NORTH", "WEST"])
  (dirReduc ["NORTH", "SOUTH", "EAST", "WEST"])
  (dirReduc  ["NORTH", "EAST", "WEST", "SOUTH", "WEST", "WEST"])
  (dirReduc  ["NORTH", "WEST", "SOUTH", "EAST"])
  (dirReduc  ["WEST" "EAST" "EAST" "WEST" "NORTH" "EAST"])

  ;;
  )

;; ===================================================

(defn merge-s-0 [s cnt]
  (loop [l s
         res []]
    (if (or
         (< (count l) cnt)
         (empty? l))
      res
      (recur
       (rest l)
       (conj res (into [(first l)] (take (dec cnt) (rest l))))))))

(defn merge-s [strarr k]
  (loop [arr strarr
         res []]
    (if (or
         (< (count arr) k)
         (empty? arr))
      res
      (recur
       (rest arr)
       (conj res (str (first arr) (apply str (take (dec k) (rest arr)))))))))

(defn longest-cons [strarr k]
  (cond
    (> k (count strarr)) ""
    (< k 1 ) ""
    :else "ok"
    )
  )

(comment
  ;;"abigailtheta"
  (longest-cons ["zone", "abigail", "theta", "form", "libe", "zas", "theta", "abigail"], 2)

  (merge-s ["zone", "abigail", "theta", "form", "libe", "zas", "theta", "abigail"] )
  (merge-s [:a :b :c :d :e] -1)
  (merge-s [] 3)
  (loop [l [:a :b :c :d :e]
         res []]
    (if (or
         (< (count l) 4)
         (empty? l))
      res
      (recur
       (rest l)
       (conj res (into [(first l)] (take (dec 4) (rest l)))))))
  ;;
  )

