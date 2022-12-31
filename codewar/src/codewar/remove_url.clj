(ns codewar.remove-url
  (:require [clojure.string :refer [split]]))

(defn remove-url-anchor [url]
  (first (split url #"#")))


(comment
  (re-matches #"(.*)#.+" "www.kata.com/path#params")
  (remove-url-anchor "www.codewars.com#about")
  (remove-url-anchor "www.codewars.com/katas/?page=1#about")
  (remove-url-anchor "www.codewars.com/katas/")
  ;;
  )