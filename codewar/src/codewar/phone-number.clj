(ns codewar.phone-number
  (:require [clojure.string :as s]))

;; https://www.codewars.com/kata/56baeae7022c16dd7400086e/train/clojure


(defn find-by-num [strng num]
  (->> strng
       s/split-lines
       (filter #(s/includes? % num))))

(defn extract-name [{:keys [line phone] :as doc}]
  (let [name-match (re-seq #"<([^>]*)>|<([^>]*)$" line)]
    (cond
      (empty? name-match)      (assoc doc :error "no name found")
      (> (count name-match) 1) (assoc doc :error (str "Too many people: " phone))
      :else (let [name (remove nil? (first name-match))]
              (assoc doc
                     :name (last name)
                     :line (s/replace line (first name) ""))))))

(defn normalize-char [c]
  (if (or (Character/isLetterOrDigit c)
          (Character/isSpace c)
          (#{\. \-} c)) 
    c 
    \space))

(defn extract-address [{:keys [line] :as doc}]
  (let [address (->> line
                     (map normalize-char)
                     (apply str)
                     s/trim)]
    (assoc doc :address (s/replace address #" +" " "))))

(defn format-line [{:keys [error phone name address]}]
  (if error
    (format "Error => %s" error)
    (format "Phone => %s, Name => %s, Address => %s" phone name address)))

(defn parse-line [line num]
  (->> {:phone num
       :line (s/replace line num "")}
      extract-name
      extract-address))

(defn phone [strng num]
  (let [line (find-by-num strng num)]
    (case (count line)
      0 (format "Error => Not found: %s" num)
      1 (format-line (parse-line (first line) num))
      (format "Error => Too many people: %s" num))))


(comment
  (def dr (str "/+1-541-754-3010 156 Alphand_St. <J Steeve>\n 133, Green, Rd. <E Kustur> NY-56423 ;+1-541-914-3010\n"
               "+1-541-984-3012 <P Reed> /PO Box 530; Pollocksville, NC-28573\n :+1-321-512-2222 <Paul Dive> Sequoia Alley PQ-67209\n"
               "+1-741-984-3090 <Peter Reedgrave> _Chicago\n :+1-921-333-2222 <Anna Stevens> Haramburu_Street AA-67209\n"
               "+1-111-544-8973 <Peter Pan> LA\n +1-921-512-2222 <Wilfrid Stevens> Wild Street AA-67209\n"
               "<Peter Gone> LA ?+1-121-544-8974 \n <R Steell> Quora Street AB-47209 +1-481-512-2222\n"
               "<Arthur Clarke> San Antonio $+1-121-504-8974 TT-45120\n <Ray Chandler> Teliman Pk. !+1-681-512-2222! AB-47209,\n"
               "<Sophia Loren> +1-421-674-8974 Bern TP-46017\n <Peter O'Brien> High Street +1-908-512-2222; CC-47209\n"
               "<Anastasia> +48-421-674-8974 Via Quirinal Roma\n <P Salinger> Main Street, +1-098-512-2222, Denver\n"
               "<C Powel> *+19-421-674-8974 Chateau des Fosses Strasbourg F-68000\n <Bernard Deltheil> +1-498-512-2222; Mount Av.  Eldorado\n"
               "+1-099-500-8000 <Peter Crush> Labrador Bd.\n +1-931-512-4855 <William Saurin> Bison Street CQ-23071\n"
               "<P Salinge> Main Street, +1-098-512-2222, Denve\n"))

  (do (= (phone dr, "48-421-674-8974"), "Phone => 48-421-674-8974, Name => Anastasia, Address => Via Quirinal Roma")
      (= (phone dr, "1-921-512-2222"), "Phone => 1-921-512-2222, Name => Wilfrid Stevens, Address => Wild Street AA-67209")
      (= (phone dr, "1-908-512-2222"), "Phone => 1-908-512-2222, Name => Peter O'Brien, Address => High Street CC-47209")
      (= (phone dr, "1-541-754-3010"), "Phone => 1-541-754-3010, Name => J Steeve, Address => 156 Alphand St.")
      (= (phone dr, "1-121-504-8974"), "Phone => 1-121-504-8974, Name => Arthur Clarke, Address => San Antonio TT-45120")
      (= (phone dr, "1-498-512-2222"), "Phone => 1-498-512-2222, Name => Bernard Deltheil, Address => Mount Av. Eldorado")
      (= (phone dr, "1-098-512-2222"), "Error => Too many people: 1-098-512-2222")
      (= (phone dr, "5-555-555-5555"), "Error => Not found: 5-555-555-5555"))
  (find-by-num dr "5-555-555-5555")


  (phone dr "+19-421-674-8974")
  (some #(when-let [match (re-matches #"(.*)\+\d\d?-\d{3}-\d{3}-\d{4}(.*)" %)]
           match)
        (s/split-lines dr))

  (some #(when (s/includes? % "+19-421-674-8974") %) (s/split-lines dr))
  (s/replace)
  (re-matches #"(.*)\+\d\d?-\d{3}-\d{3}-\d{4}(.*)" "/+1-541-754-3010 156 Alphand_St. <J Steeve>")
  (re-matches #"(.*)\+\d\d?-\d{3}-\d{3}-\d{4}(.*)" "/+1-541-754-3010 156 Alphand_St. <J Steeve")

  (re-seq #"<[^>]*>|<[^>]*$" "sdfg<R Steell> Quora Street AB-4<sdd> sdfgsd<sdd")
  (->> (re-seq #"<([^>]*)>|<([^>]*)$" "sdfg<R Steell> Quora Street AB-4<sdd> sdfgsd<sdd")
       (map (partial remove nil?))
       last)


  ;;
  )