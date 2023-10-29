(ns codewar.denico)

;; https://www.codewars.com/kata/596f610441372ee0de00006e/train/clojure


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


(defn str->grid [col-count s]
  
  )

(comment

  (def s1 (partition 5 5 "cseerntiofarmit on  "))

  (loop [parts s1
         grid (take (count (first s1)) (repeat []))]
    (if (empty? (first parts))
      grid
      (recur (map rest s1)
             ())))

  ;;
  )
(defn denico [k message]
  (if (= 1 (count k))
    m
    (let [nkey (create-numeric-key k)]
      (->> message
           (str->to-grid (count nkey))
           (re-order nkey)
           (grid->str (count nkey))))))

(comment

  (denico "crazy" "cseerntiofarmit on  ")
  ;;
  )