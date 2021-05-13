(ns run-length-encoding)

(defn run-length-encode
  "encodes a string with run-length-encoding"
  [plain-text]
  (reduce #(str %1 (if (= 1 (count %2))
                     (str (first %2))
                     (str (count %2) (first %2))))
          ""
          (partition-by identity plain-text)))

(defn run-length-decode
  "decodes a run-length-encoded string"
  [cipher-text]
  (reduce #(str %1 (if (= 1 (count %2))
                     %2
                     (let [match (first (re-seq #"(\d+)\D" %2))
                           cnt   (Integer/parseInt (last match))
                           lettr (last %2)]
                       (apply str (repeat cnt lettr)))))
          ""
          (re-seq #"\d+\D|\D" cipher-text)))

