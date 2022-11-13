(ns codewar.bookseller
  (:require [clojure.string :as s]))

;; https://www.codewars.com/kata/54dc6f5a224c26032800005c/train/clojure

;; first solution
(defn stock-list [list-of-books list-of-cat]
  (cond
    (empty? list-of-books)     []
    (empty? list-of-cat)       []
    :else  (map (fn [cat]
                  (vector cat (->> list-of-books
                                   (filter #(.startsWith % cat))
                                   (map #(->> (s/split % #" ")
                                              (second)
                                              (Integer/parseInt)))
                                   (apply +))))
                list-of-cat)))


(comment
  (stock-list [] ["A" "B"])

  ;;
  )



(defn stock-list-2 [list-of-books list-of-cat]
  (let [unorder-stock-list (reduce (fn [res book]
                                     (update res (str (first book)) (fnil + 0) (Integer/parseInt (second  (s/split book #" ")))))
                                   {}
                                   list-of-books)]
    (map #(vector % (or (get unorder-stock-list %) 0)) list-of-cat)))


(defn stock-list-3 [list-of-books list-of-cat]
  (let [books (map #(let [[_ book-code book-quantity-str] (re-matches #"([A-Z]+) (\d+)" %)]
                      (vector (str (first book-code)) (Integer/parseInt book-quantity-str))) list-of-books)
        unorder-stock-list (reduce (fn [res [book-code book-quantity]]
                                     (update res book-code (fnil + 0) book-quantity))
                                   {}
                                   books)]
    (map #(vector % (or (get unorder-stock-list %) 0)) list-of-cat)))

(comment
  (def ur ["BBAR 150", "CDXE 515", "BKWR 250", "BTSQ 890", "DRTY 600"])
  (def vr ["A" "B" "C" "D"])

  (map #(let [[_ book-code book-quantity-str] (re-matches #"([A-Z]+) (\d+)" %)]
          (vector (str (first book-code)) (Integer/parseInt book-quantity-str))) ur)

  (re-matches #"([A-Z]+) (\d+)" "AB 12")

  (reduce (fn [res book]
            (update res (str (first book)) (fnil + 0) (Integer/parseInt (second  (s/split book #" "))))) {} ur)

  (def unordered {"B" 1290, "C" 515, "D" 600})
  (map #(vector % (or (get unordered %) 0)) vr)

  (str \A)
  (update {} :k (fnil + 0) 1)
  (update {:k 12} :k (fnil + 0) 1)

  (stock-list ur vr)
  (map (fn [cat]
         (vector cat)) vr)

  (.startsWith "hello" "h")
  (filter #(.startsWith % "B") ur)

  (->> (s/split "BBB 150" #" "))
  (Integer/parseInt (second (->> (s/split "BBB 150" #" "))))


  (->> ["BBAR 150", "CDXE 515", "BKWR 250", "BTSQ 890", "DRTY 600"]
       (filter #(.startsWith % "B"))
       (map #(->> (s/split % #" ")
                  (second)
                  (Integer/parseInt)))
       (apply +))




  ;;
  )