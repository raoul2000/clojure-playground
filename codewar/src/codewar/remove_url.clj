(ns codewar.remove-url
  (:require [clojure.string :refer [split]]))

(defn remove-url-anchor [url]
  (first (split url #"#")))

;; first commit in 2023 !!! yuuupui !!


(comment
  (re-matches #"(.*)#.+" "www.kata.com/path#params")
  (remove-url-anchor "www.codewars.com#about")
  (remove-url-anchor "www.codewars.com/katas/?page=1#about")
  (remove-url-anchor "www.codewars.com/katas/")
  ;;
  )