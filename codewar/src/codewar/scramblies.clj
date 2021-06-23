(ns codewar.scramblies)

;; https://www.codewars.com/kata/55c04b4cc56a697bb0000048/train/clojure

(defn scramble-1 [s1 s2]
  (->> s2
       (reduce (fn [acc c]
                 (if-let [cnt (get acc c)]
                   (assoc acc c (dec cnt))
                   (assoc acc c -1)))
               (frequencies s1))
       vals
       (some neg?)
       not))

(defn scramble [s1 s2]
  (->> s2
       (reduce #(assoc %1 %2 (dec (or (get %1 %2) 0))) (frequencies s1))
       vals
       (some neg?)
       not))

(comment

  ;; character frequencies
  (frequencies "rkqodlw")

  ;; reduce: fails on missing char
  (reduce (fn [acc c]
            (if-let [cnt (get acc c)]
              (assoc acc c (dec cnt))
              (assoc acc c -1))) (frequencies "katas") "steak")

  ;; test map values
  (vals {:a 1 :b 0})

  (some neg? [1 2 -1 0])
  (any? neg? [1 2 1 1])

  (data/diff (vec "rkqodlw") (vec "world"))
  (scramble "rkqodlw", "world") ;; ,true)
  (scramble "cedewaraaossoqqyt", "codewars") ;;true)
  (scramble "katas", "steak") ;;false)
  (scramble "scriptjavx", "javascript") ;;false)
  (scramble "scriptingjava", "javascript") ;;true)
  (scramble "scriptsjava", "javascripts") ;;true)
  (scramble "javscripts", "javascript") ;;false)
  (scramble "aabbcamaomsccdd", "commas") ;;true)
  (scramble "commas", "commas") ;;true)
  (scramble "sammoc", "commas") ;;true)

  ;;
  )