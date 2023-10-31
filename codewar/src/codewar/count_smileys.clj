(ns codewar.count-smileys)

;; see https://www.codewars.com/kata/583203e6eb35d7980400002a/train/clojure


(comment
  ;; couting smileys using regex



  (count (filter #(re-matches #"[:;][-~]?[\)D]" %) [":)"  ";("  ";}"  ":-D"]))
  ;;
  )


(defn count-smileys [arr]
  (count (filter #(re-matches #"[:;][-~]?[\)D]" %) arr)))