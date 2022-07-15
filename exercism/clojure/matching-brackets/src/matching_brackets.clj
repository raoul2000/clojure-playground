(ns matching-brackets)



(def bracket-pair {\[ \]
                   \{ \}
                   \( \)})

(defn opening-bracket? [c]
  (boolean (get bracket-pair c)))

(defn closing-bracket? [c]
  (some (partial = c) (vals bracket-pair)))

(defn closing-bracket [c]
  (get bracket-pair c))

(comment
  (opening-bracket? \()
  (opening-bracket? \))


  (reduce (fn [lifo c]
            (let [closer (closing-bracket c)] ;; c is opener
              (cond
                closer                     (conj lifo closer)
                (closing-bracket? c)       (if (= (last lifo) c)
                                             (pop lifo)
                                             (reduced (conj lifo c)))
                :else                      lifo)))
          [] "{[][]}")
  ;;
  )

(defn valid? [s]
  (->> (reduce (fn [lifo c]
                    (let [closer-char (closing-bracket c)]
                      (cond
                        closer-char           (conj lifo closer-char)
                        (closing-bracket? c)  (if (= (last lifo) c)
                                                (pop lifo)
                                                (reduced (conj lifo c)))
                        :else                 lifo)))
                  [] s)
       empty?))
