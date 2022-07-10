(ns meetup-draft
  (:import [java.time LocalDate DayOfWeek]))

(comment
  (LocalDate/of 2013 5 1)
  (.getDayOfWeek (LocalDate/of 2013 5 1))
  (.getMonthValue (LocalDate/of 2013 5 1))
  (= DayOfWeek/WEDNESDAY (.getDayOfWeek (LocalDate/of 2013 5 1)))
  (= DayOfWeek/SUNDAY (.getDayOfWeek (LocalDate/of 2013 5 1)))


  (-> (LocalDate/of 2013 5 1)
      (.plusDays 1))

  (take 4 (iterate #(.plusDays % 1) (LocalDate/of 2013 5 1)))
  (first (drop-while #(not= DayOfWeek/MONDAY (.getDayOfWeek %))  (iterate #(.plusDays % 1) (LocalDate/of 2013 5 1))))

  (first (drop-while #(not= DayOfWeek/MONDAY (.getDayOfWeek %))  (iterate #(.plusDays % 1) (LocalDate/of 2022 7 1))))

  ;; find date of the first given day of week in given month/year  
  (->> (LocalDate/of 2022 7 1)
       (iterate #(.plusDays % 1))
       (drop-while #(not= DayOfWeek/MONDAY (.getDayOfWeek %)))
       first)

  ;;
  )

(def days-of-week {:monday    DayOfWeek/MONDAY
                   :tuesday   DayOfWeek/TUESDAY
                   :wednesday DayOfWeek/WEDNESDAY
                   :thursday  DayOfWeek/THURSDAY
                   :friday    DayOfWeek/FRIDAY
                   :saturday  DayOfWeek/SATURDAY
                   :sunday    DayOfWeek/SUNDAY})

(defn- find-first-dow [year month day-of-week]
  (let [dow (get days-of-week day-of-week)]
    (->> (LocalDate/of year month 1)
         (iterate #(.plusDays % 1))
         (drop-while #(not= dow (.getDayOfWeek %)))
         first)))

(comment
  (find-first-dow 2022 07 :friday)
  (.plusWeeks (find-first-dow 2022 07 :friday) 1)
  (.plusWeeks (find-first-dow 2022 07 :friday) 4)
  (.plusWeeks (find-first-dow 2022 07 :friday) 5)
  (.getMonthValue (find-first-dow 2022 07 :friday))


  (take-while #(= 7 (.getMonthValue %)) (iterate #(.plusWeeks % 1) (find-first-dow 2022 07 :friday)))

  ;;
  )

(defn same-month [month ^java.time.LocalDate date]
  (= month (.getMonthValue date)))

(defn find-all-dow-in-month [^LocalDate first-dow]
  (let [in-same-month? (partial same-month (.getMonthValue first-dow))]
    (->> first-dow
         (iterate #(.plusWeeks % 1))
         (take-while in-same-month?)
         (mapv #(.getDayOfMonth %)))))

(comment
  (find-all-dow-in-month (find-first-dow 2022 07 :friday))
  ;;
  )

(comment
  (defn teenth? [n] (> 20 n 12))
  (def not-teenth? (complement teenth?))

  (teenth? 10)
  (teenth? 20)
  (teenth? 19)
  (not-teenth? 1)

  (def selectors {:first   first
                  :second  second
                  :third   #(get % 2)
                  :fourth  #(get % 3)
                  :fifth   #(get % 4)
                  :last    last
                  :teenth  #(first (drop-while not-teenth? %))})

  ((:first selectors) [1 2 3])
  ((:third selectors) [1 2 3])

  (drop-while not-teenth? [1 2 3 15 22])
  ((:teenth selectors) [1 2 3 13 25])

  ;;
  )

(defn teenth? [n] (> 20 n 12))
(def not-teenth? (complement teenth?))

(def selectors {:first   first
                :second  second
                :third   #(get % 2)
                :fourth  #(get % 3)
                :fifth   #(get % 4)
                :last    last
                :teenth  #(first (drop-while not-teenth? %))})

(defn meetup [month year day-of-week descriptor] ;; <- arglist goes here
  (let [starter-day (find-first-dow year month day-of-week)
        selector    (get selectors descriptor)
        candidates  (find-all-dow-in-month starter-day)]
    [year month (selector candidates)]))