(ns word-count.play)


(defn word-count [s]
  (->> s
       clojure.string/lower-case
       (re-seq #"[\w\d]+")
       
       (frequencies)))

(comment
  (def matcher (re-matcher #"[\w\d]+" "eer abc"))
  (re-find matcher)

  (re-seq #"[\w\d]+" "eer abc")
  (clojure.string/lower-case "aBbc")

  (->> "abc def ghGGG , fsdg"
       (re-seq #"[\w\d]+")
       (map clojure.string/lower-case)
       (frequencies))

  (word-count "testing, 1, 2 testing")

  (re-groups matcher)

  (re-groups (re-matcher #"([[:alnum:]]+)" "eer"))
  (re-groups #"([[:alnum:]]+)" "eer")

  ;;
  )