(ns grade-school)

(def roster (atom {}))

(defn add [db name grade]  ;; <- arglist goes here
  (if-let [students (db grade)]
    (assoc db grade (conj students name))
    (assoc db grade [name])))

(comment
  (add "bob" 2 roster)
  (-> {}
      (add "bob" 2)
      (add "ali" 2))
  ;;
  )


(defn grade [school grade]  ;; <- arglist goes here
  (if-let [result (get school grade)]
    result
    []))

(comment
  (-> {}
       (add "bob" 2)
       (grade 1))
  (-> {}
      (grade-school/add "Franklin" 5)
      
      (grade-school/add "Bradley" 5)
      (grade-school/add "Jeff" 1)
      (grade-school/grade 5))
  ;;
  )

(defn sorted [school]  ;; <- arglist goes here
   (into (sorted-map) school))

(comment
  (map #())
  (sorted {2 ["c" "d"] 1 ["e" "b" "a"]})
  
  ;;
  )



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
       (sorted))


  ;;
  )
