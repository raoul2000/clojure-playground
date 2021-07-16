(ns grade-school)

(def db (atom {}))

(defn grade [grade school]  ;; <- arglist goes here
  (school grade))


(defn add-atom-solution [name grade]  ;; <- arglist goes here
  (swap! db (fn [db] (if-let [students (db grade)]
                       (assoc db grade (sort (conj students name)))
                       (assoc db grade [name])))))

(defn add [name grade db]  ;; <- arglist goes here
  (if-let [students (db grade)]
    (assoc db grade (sort (conj students name)))
    (assoc db grade [name])))

(defn sorted [school]  ;; <- arglist goes here
  (into (sorted-map) school))


(comment
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
         (sorted ))

  
  ;;
  )
