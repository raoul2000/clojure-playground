(ns codewar.observedpin)




;;
;;┌───┬───┬───┐
;;│ 1 │ 2 │ 3 │
;;├───┼───┼───┤
;;│ 4 │ 5 │ 6 │
;;├───┼───┼───┤
;;│ 7 │ 8 │ 9 │
;;└───┼───┼───┘
;;    │ 0 │
;;    └───┘

(def adj-key {\1 [\2 \4]
              \2 [\1 \3 \5]
              \3 [\2 \6]
              \4 [\1 \5 \7]
              \5 [\2 \4 \6 \8]
              \6 [\3 \5 \9]
              \7 [\4 \8]
              \8 [\5 \7 \9 \0]
              \9 [\6 \8]
              \0 [\8]})

(defn get-possible-keys [^Character k]
  (into [k] (get adj-key k)))


(defn combine-keys [[first-keys & remaining-keys]]
  (if (empty? remaining-keys)
    (map vector first-keys)
    (reduce (fn [res code]
              (for [p1 res
                    p2 code]
                (flatten [p1 p2]))) first-keys remaining-keys)))

(defn get-pins [observed-coll]
  (->> observed-coll
       (map get-possible-keys)
       (combine-keys)
       (map #(apply str %))))

(comment

  (get-pins "369")
  (get-pins "1")
  (get-pins "00")
  (count (get-pins "12345678"))
  (map get-possible-keys '("1" "2" "3"))

  (def pins [["2" "6"] ["5" "9"] ["6" "8"]])
  (combine-keys pins)

  (reduce (fn [res code]
            (for [p1 res
                  p2 code]
              (flatten [p1 p2]))) (first pins) (rest pins))


  (get-possible-keys \3)
  (map get-possible-keys [\3])
  (for [p1 ["2" "6"]
        p2 ["3" "5" "9"]]
    (flatten [p1 p2]))


  (for [p1 '(("2" "3") ("2" "5") ("2" "9") ("6" "3") ("6" "5") ("6" "9"))
        p2 ["6" "8"]]
    (flatten [p1 p2]))

  ;;
  )