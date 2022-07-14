(ns diamond)

;; to create the diamond we create only the top right quarter
;; skipping first line, then mirror vertical and  mirror horizontal
;; to get body lines.
;; Last, enclose the body lines with same start/end line.

(defn diamond-size [c]
  (let [letter-pos (- (int c) 64)]
    (inc (* 2 (dec letter-pos)))))

(defn make-empty-line [len]
  (into [] (repeat len " ")))

(defn make-top-right-quarter [len]
  (let [empty-line (make-empty-line len)]
    (map #(assoc empty-line  % (char (+ 66 %))) (range 0 len))))

(defn mirror-vertical [lines]
  (map #(concat (reverse %) [" "] %) lines))

(defn mirror-horizontal [lines]
  (concat lines (reverse (butlast lines))))

(defn make-first-line [size]
  (assoc (make-empty-line size)  (quot size 2) \A))

(defn enclose [size body-lines]
  (let [first-line (make-first-line size)]
    (concat [first-line] body-lines [first-line])))

(defn diamond [c]
  (if (= \A c)
    ["A"]
    (let [size (diamond-size c)]
      (->> (make-top-right-quarter (quot size 2))
           mirror-vertical
           mirror-horizontal
           (enclose size)
           (map #(apply str %))))))


