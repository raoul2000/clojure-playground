(ns four-clojure.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

;; Write a function which takes a vector of keys and a vector of values and constructs a map from them.

(defn mrg [ks vs]
  (into '{} (map #(vector %1 %2)  ks vs)))

(comment
  (mrg  [:a :b :c] [1 2 3])
  (into '{} (map #(vector %1 %2)  [:a :b :c :d] [1 2 3]))
  (mrg   [:a :b :c :d] [1 2 3])
  (into '{} '([:a 1] [:b 2] [:c 3]))
  
  )

;; Given two integers, write a function which returns the greatest common divisor.

(defn pgdd [n1 n2]
  (first (filter #(= [(mod n1 %) (mod n2 %)] [0 0]) (range (max n1 n2) 0 -1))))

(comment
  (range 1 10)
  (pgdd 1023 858)
  (pgdd 2 4)
  (pgdd 5 7)
  (doseq [i (range  50 1 -1)
          :when (= [(mod 1023 i) (mod 858 i)] [0 0])]
          (prn i))
  (first (filter #(= [(mod 1023 %) (mod 858 %)] [0 0]) (range 50 1 -1)) )
  )