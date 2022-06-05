(ns gigasecond
  (:import [java.time LocalDateTime]))


(defn from-1
  "Return a 3-uplet representing the date in one gigasecond from the given 
   date"
  [year month day]
  (let [date         (java.time.LocalDateTime/of year month day 0 0)
        date+gigasec (.plusSeconds date 1000000000)]
    [(.getYear       date+gigasec)
     (.getMonthValue date+gigasec)
     (.getDayOfMonth date+gigasec)]))


;; first solution works fine. Let's try to refactor it to make it
;; more readable and get read of local bindings

(def giga-second 1e9)

(defn from
  "Return a 3-uplet representing the date [year month day] in one gigasecond 
   from the given date."
  [year month day]
  (-> (LocalDateTime/of year month day 0 0)
      (.plusSeconds giga-second)
      (bean)
      ((juxt :year :monthValue :dayOfMonth))))
