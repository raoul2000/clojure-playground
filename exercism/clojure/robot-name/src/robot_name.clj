(ns robot-name)

(defn rand-letter []
  (char (+ 65 (rand-int 26))))

(defn rand-num []
  (char (+ 48 (rand-int 10))))

(defn rand-name []
  (apply str (concat (repeatedly 2 rand-letter) (repeatedly 3 rand-num))))

(defn robot [] 
  (atom {:name (rand-name)}))

(defn robot-name [robot] 
  (:name @robot))

(defn reset-name [robot]
  (swap! robot #(assoc % :name (rand-name))))
