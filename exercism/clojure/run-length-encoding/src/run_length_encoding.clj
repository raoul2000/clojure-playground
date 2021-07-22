(ns run-length-encoding)

(defn encode-it [[fst & rst :as char-seq]]
  (cond->> fst
    rst (str (count char-seq))))

(defn run-length-encode
  "encodes a string with run-length-encoding"
  [plain-text]
  (->>  plain-text
        (partition-by identity)
        (map encode-it)
        (apply str)))

(defn decode-it
  [[_ cnt c]]
  (cond->> c
    cnt (repeat (Integer/parseInt cnt))))

(defn run-length-decode
  "decodes a run-length-encoded string"
  [cipher-text]
  (->>  cipher-text
        (re-seq #"(\d+)?(\D)")
        (mapcat decode-it)
        (apply str)))

