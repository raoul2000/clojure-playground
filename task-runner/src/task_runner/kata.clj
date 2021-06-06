(ns task-runner.kata
  (:require [clojure.string :as s]
            [clojure.data :as d]))

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

;; much better with (partition k 1 ...)
(defn longest-cons [strarr k]
  (if (< k 1)
    ""
    (loop [arr     strarr
           result  ""]
      (if (or
           (< (count arr) k)
           (empty? arr))
        result
        (recur
         (rest arr)
         (let [candidate (str (first arr) (apply str (take (dec k) (rest arr))))]
           (if (> (count candidate) (count result))
             candidate
             result)))))))


;; ===================================================

(defn array-diff [a b]
  (loop [l b
         res a]
    (if (empty? l)
      res
      (recur
       (rest l)
       (filter #(not= % (first l)) res)))))

(defn array-diff-best [a b]
  (remove (set b) a))

;; ==========================================

(defn dna-strand-1 [dna]
  (apply str (map #(cond
                     (= \A %) \T
                     (= \T %) \A
                     (= \C %) \G
                     (= \G %) \C) dna)))

(def dna->complement {\A \T
                      \T \A
                      \C \G
                      \G \C})
(defn dna-strand [dna]
  (apply str (map dna->complement dna)))

(comment
  (dna-strand "ATCG")
  (dna-strand "AAAA")
  ;;
  )

;; ==========================================

(defn encrypt [st n]
  (if (< n 1)
    st
    (recur
     (apply str (flatten (concat (partition 1 2 (rest st)) (partition 1 2 st))))
     (dec n))))

(defn f1 [s]
  (partition-by #(even? (first %)) (map-indexed vector s)))

(defn f2 [s]
  (reduce #(if (even? (first %2))
             [(first %1) (str (second %1) (second %2))]
             [(str (first %1) (second %2)) (second %1)]) ["" ""] (map-indexed vector s)))

(comment
  (f1 "abcde")
  (map-indexed vector "abcde")
  (f2 "This is a test!")
  ;;
  )

(comment
  (partition 1 2 (rest "abcd"))
  (apply str (flatten (concat (partition 1 2 (rest "This is a test!")) (partition 1 2 "This is a test!"))))
  (reduce-kv (fn [res k v]
               (if (even? k))) [] (into [] "This is a test!"))

  ;; "abc" => {1 \a 2 \b 3 \c}
  (map-indexed vector "abc") ;; => ([0 \a] [1 \b] [2 \c])
  (partition-by #(even? (first %)) '([0 \a] [1 \b] [2 \c] [3 \d]))
  ;;
  )

(defn decrypt-1 [st n]
  (if (< n 1)
    st
    (let [s st
          half (quot (count s) 2)]
      (recur
       (apply str (flatten (map vector (drop half s) (take half s))))
       (dec n)))))

(defn decrypt [st n]
  (if (< n 1)
    st
    (let [s st
          half (quot (count s) 2)]
      (recur
       (apply str (interleave (drop half s) (conj (take half s) nil)))
       (dec n)))))

(comment
  (let [s "hsi  etti sats"
        half (quot (count s) 2)]
    [(drop half s)
     (take half s)])

  (encrypt "this is a test" 1)
  (apply str  (interleave '(\t \i \space \s \a \t \s) '(\h \s \i \space \space \e \t)))
  (interleave '(\t \i \space \s \a \t \s) '(\h \s \i \space \space \e \t \!))

  (let [s "hsi  etti sats"
        half (quot (count s) 2)]
    (apply str (flatten (map vector (drop half s) (take half s)))))

  (encrypt "ab!" 1)
  (decrypt "ba!" 1)

  (let [s "hsi  etti sats"]
    (split-at (quot (count s) 2) s))

  ;;
  )
