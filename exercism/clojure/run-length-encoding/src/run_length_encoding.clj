(ns run-length-encoding)

(defn encode-it [char-seq]
  (if (= 1 (count char-seq))
    (str (first char-seq))
    (str (count char-seq) (first char-seq))))

(defn run-length-encode
  "encodes a string with run-length-encoding"
  [plain-text]
  (->> plain-text
       (partition-by identity)
       (map encode-it)
       (apply str)))

(defn decode-it [[_ cnt letter]]
  (apply str (repeat (Integer/parseInt (or cnt "1")) letter)))

(defn run-length-decode
  "decodes a run-length-encoded string"
  [cipher-text]
  (->> cipher-text
       (re-seq #"(\d+)?(\D)")
       (map decode-it)
       (apply str)))

