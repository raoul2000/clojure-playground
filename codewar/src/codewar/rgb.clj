(ns codewar.rgb
  (:require [clojure.string :as string]))

;; https://www.codewars.com/kata/513e08acc600c94f01000001/train/clojure

(defn rgb-1 [r g b]
  (->> [r g b]
       (map #(if (< % 0) 0 %))
       (map #(if (> % 255) 255 %))
       (map #(Integer/toHexString %))
       (map #(if (= 1 (count %))
               (str "0" %)
               %))
       (apply str)
       (string/upper-case)))

(defn zero-right-pad [s]
  (if (= 2 (count s))
    s
    (->> s
         vec
         (cons \0)
         (take-last 2)
         (apply str))))

(defn int->hex [n]
  (cond
    (< n 0)   "00"
    (> n 255) "FF"
    :else     (->> n
                   Integer/toHexString
                   string/upper-case
                   zero-right-pad)))

(defn rgb [r g b]
  (apply str (map int->hex [r g b])))


(comment
  ;; ------------------------------------
  ;; ..from solution
  ;; much better : use (format "%02X" 15)
  ;; use (max 0 n) and (min 255 n) instead of (if ...)
  ;; ------------------------------------


  ;; zero left pad string  
  (apply str (take-last 2 (conj '("F") "0")))

  ;; call toHexString in thread last
  (->> 15
       Integer/toHexString
       string/upper-case
       vec
       (cons \0)
       (take-last 2)
       (apply str))

  (int->hex 255)
  (= "000000" (rgb 0 0 0))
  (= "000000" (rgb 0 0 -20))
  (= "FFFFFF" (rgb 300 255 255))
  (= "ADFF2F" (rgb 173 255 47))
  (= "9400D3" (rgb 148 0 211))

  ;; int to hex string
  (Integer/toHexString 255)
  (Integer/toHexString -20)
  (Integer/toHexString 300)

  ((comp string/upper-case #(Integer/toHexString %)) 255)


  ;; left pad with zero
  (format "%02d" "F"))