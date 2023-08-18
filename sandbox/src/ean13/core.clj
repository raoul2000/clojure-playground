(ns ean13.core
  "EAN13 code bar creation based on https://barcode-coder.com/en/ean-13-specification-102.html
   
   A binary value is produced but it can't be rendered using the EAN13 font.
   ")



(def tab-selector [[0 0 0 0 0 0]
                   [0 0 1 0 1 1]
                   [0 0 1 1 0 1]
                   [0 0 1 1 1 0]
                   [0 1 0 0 1 1]
                   [0 1 1 0 0 1]
                   [0 1 1 1 0 0]
                   [0 1 0 1 0 1]
                   [0 1 0 1 1 0]
                   [0 1 1 0 1 0]])

(def table-left-part [["0001101"
                       "0011001"
                       "0010011"
                       "0111101"
                       "0100011"
                       "0110001"
                       "0101111"
                       "0111011"
                       "0110111"
                       "0001011"]

                      ["0100111"
                       "0110011"
                       "0011011"
                       "0100001"
                       "0011101"
                       "0111001"
                       "0000101"
                       "0010001"
                       "0001001"
                       "0010111"]])

(def table-right-part ["1110010"
                       "1100110"
                       "1101100"
                       "1000010"
                       "1011100"
                       "1001110"
                       "1010000"
                       "1000100"
                       "1001000"
                       "1110100"])


(defn string->coll-int [s]
  {:pre [(re-matches #"[0-9]+" s)]}
  (map #(Character/digit % 10) s))

(comment
  (string->coll-int "1234")
  (string->coll-int "A234")
  ;;
  )

(defn sum [coll]
  (->> (partition-all 1 2 coll)
       (flatten)
       (apply +)))

(defn sum-odd-index
  "sum numbers at 1 based odd index: 1, 3, 5, ..."
  [coll]
  (sum  coll))

(defn sum-even-index
  "sum numbers at 1 based even index : 2, 4, 6, ..."
  [coll]
  (sum (rest coll)))

(defn checksum
  "Given a string of 12 digits, returns the EAN13 checksum"
  [coll]
  {:pre [(= 12 (count coll))]}
  (let [i    (sum-odd-index  coll)
        p    (sum-even-index coll)
        i+3p (+ i (* 3 p))]
    (mod (- 10 (mod i+3p 10)) 10)))


(comment
  (checksum (string->coll-int "378280850095"))
  ;; => 7
  (checksum (string->coll-int "379280850095"))
  ;; => 6
  (checksum (string->coll-int "389280850095"))
  ;; => 3
  ;;
  )

(defn create-ean-string [{:keys [press-code publication-code price]}]
  (let [code-no-control-key    (string->coll-int (str press-code publication-code price))
        complete-code          (conj (vec code-no-control-key) (checksum code-no-control-key))
        [left-part right-part] (split-at 7 complete-code)
        part-1                 (map (fn [n tab-num]
                                      (-> table-left-part
                                          (get  tab-num)
                                          (get  n))) (rest left-part) (get tab-selector (first left-part)))
        part-2                 (map #(get table-right-part %) right-part)
        str-binary             (str "101"
                                    (apply str part-1)
                                    "01010"
                                    (apply str part-2)
                                    "101")]
    (->> (partition-all 8 8 str-binary)
         (map #(apply str %)))))


(comment

  (create-ean-string {:press-code "210"
                      :publication-code "98765"
                      :price "4321"
                      :parution-number "09090"})
;; part 1
;;  0011001 0001101 0010111 0001001 0111011 0000101 = resuklt
;;  0011001 0001101 0010111 0001001 0111011 0000101 = expected 

;; part 2
;; 1001110 1011100 1000010 1101100 1100110 1110010 = result
;; 1001110 1011100 1000010 1101100 1100110 1110010 = expected

  ;;
  )

(comment
  (def result-as-binary '("10100110"
                          "01000110"
                          "10010111"
                          "00010010"
                          "11101100"
                          "00101010"
                          "10100111"
                          "01011100"
                          "10000101"
                          "10110011"
                          "00110111"
                          "0010101"))

  (->> result-as-binary
       (map #(Integer/parseInt % 2))
       (map char)
       (apply str))
  (char 166)

  ;;
  )