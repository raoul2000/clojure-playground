(ns codewar.parse-html-color)

;; https://www.codewars.com/kata/58b57ae2724e3c63df000006/train/clojure

(def preset-colors {"LimeGreen" "#FF0AB1"})

(defn hex-seq->map
  [xs]
  (zipmap [:r :g :b] (map #(Integer/parseInt % 16)  xs)))

(defn clone-char [xs]
  (map (fn [c] (str c c)) xs))

(defn parse-html-color [color]
  (condp (comp seq re-seq) (clojure.string/upper-case color)
    #"#(\w\w)(\w\w)(\w\w)" :>> #(hex-seq->map (rest (first %1)))
    #"#(\w)(\w)(\w)"       :>> #(hex-seq->map (clone-char (rest (first %1))))
    (parse-html-color (get preset-colors (clojure.string/lower-case color)))))

(comment
  (parse-html-color "#FA1")
  ;; 2 digit hexa number to decimal

  ;; use a map
  (def hexa {\A 10
             \B 11})
  ;;.. or char value
  (identity (- (int \A) 55))

  ;; convert hex to dec
  (->> "FF15"
       (map #(if (Character/isDigit %)
               (- (int %) 48)
               (- (int %) 55)))
       (reverse)
       (map-indexed #(* %2 (Math/pow 16 %1)))
       (apply +)
       int)

  ;; paritiion 6 char hex 
  (partition 2 "abcd")
  (partition 1 "abcd")

  ;; extract using RE match group
  (re-matches #"\#(\d\d?)(\d\d?)(\d\d?)" "#223423")
  (re-matches #"\#(\w\w?)(\w\w?)(\w\w?)" "#01F")

  ;; remove first char
  (rest "#abc")
  (get  preset-colors '"LimeGreen")

  ;; double each char in string
  (map #(str % %) "ab")


  ;;
  )