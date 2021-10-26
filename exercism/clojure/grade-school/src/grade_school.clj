(ns grade-school)


(defn add [db name grade]
  (let [students (get db grade [])]
    (assoc db grade (conj students name))))


(defn grade [school grade]
  (get school grade []))

(defn sorted [school]
  (reduce (fn [acc [k v]]
            (assoc acc k (sort v)))
          (sorted-map)
          school))




(comment
  (into {} [1 2])
  ;; convert a string to a keyword
  (let [s "12"]
    (keyword (str s)))

  ;; ..but not needed as an int can be used as map keu
  (def db {1 ["a" "b" "c"]
           3 ["e" "r" "a"]})

  (assoc {} 1 ["abc"])
  ({1 ["abc"]} '12)
  ;; using if-let
  (if-let [x :e]
    :something
    :else)
  (->> {}
       (add "bob" 1)
       (add "Alice" 1)
       (add "Albert" 1)
       (grade 1))


  (->> {}
       (add "bob" 1)
       (add "Alice" 1)
       (add "Albert" 1)
       (add "Zoe" 2)
       (add "Bernard" 2)
       (add "bill" 1)
       (sorted))


  ;;
  )
