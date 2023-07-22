(ns squeaky-clean
  (:require [clojure.string :as str]))

;; (almost) no regex !

(defn control-char? [c]
  (let [n (int c)]
    (or (<= 0x0000 n 0x001F)
        (<= 0x007F n 0x009F))))


(defn replace-control-chars-with-CTRL [s]
  (->> s
       (map #(if (control-char? %) "CTRL" (Character/toString %)))
       (apply str)))

(defn replace-space-with-underscore [s]
  (str/replace s #" " "_"))

(defn kebab-case->camel-case [s]
  (let [[head & tail]  (str/split s #"-")
        capitalize (fn [s] (str (Character/toUpperCase (first s)) (subs s 1)))]
    (->> tail
         (map #(capitalize %))
         (apply str)
         (str head))))

(comment

  (def s "abCde")
  (str (Character/toUpperCase (first s)) (apply str (rest s)))
  (str (str/upper-case (first s)) (apply str (rest s)))
  (str (str/upper-case (first s)) (subs s 1))
  (subs "ert" 1)
  ()
  (Character/toUpperCase \a)
  (str \a "e")
  (str/upper-case "azeRze")
  ;;
  )



(defn omit-characters-that-are-not-letters [s]
  (->> s
       (filter #(or (Character/isAlphabetic (int %))
                    (= \_ %)))
       (apply str)))


(defn  omit-greek-lower-case-letters [s]
  (->> s
       (remove #(<= (int \α) (int %) (int \ω)))
       (apply str)))

(defn clean
  [s]
  (if (str/blank? s)
    ""
    (-> s
        kebab-case->camel-case
        replace-space-with-underscore
        omit-greek-lower-case-letters
        replace-control-chars-with-CTRL
        omit-characters-that-are-not-letters)))



(comment
;;e 'α' to 'ω'.


  (clean "9 -abcĐ😀ω\0")
  ;; expected : _AbcĐCTRL  actual : _Abcđctrl


  (def s3 "9 -abcĐ😀ω\0")

  (->> s3
       kebab-case->camel-case
       replace-space-with-underscore
       replace-control-chars-with-CTRL)


  (kebab-case->camel-case "_-ab")
  (def s2 "1😀2_3az")

  (->> s2
       (filter #(or (Character/isAlphabetic (int %))
                    (= \_ %)))
       (apply str))

  (def s1 "ab-cd-efg")

  (let [[head & tail]  (str/split s1 #"-")]
    (str head (apply str (map #(str/capitalize %) tail))))

  ;;
  )