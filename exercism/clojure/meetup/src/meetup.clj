(ns meetup
  (:import [java.time LocalDate DayOfWeek]))

(def days-of-week {:monday    DayOfWeek/MONDAY
                   :tuesday   DayOfWeek/TUESDAY
                   :wednesday DayOfWeek/WEDNESDAY
                   :thursday  DayOfWeek/THURSDAY
                   :friday    DayOfWeek/FRIDAY
                   :saturday  DayOfWeek/SATURDAY
                   :sunday    DayOfWeek/SUNDAY})

(defn- find-first-dow 
  "Given a day of week, returns the first Date for this day in given month and year.
   *day-of-week* must be a key in map *days-of-week*.

   Example: returns the first friday in July 2022
   ```
   (find-first-dow 2022 07 :friday)
   => #object[java.time.LocalDate 0x31475985 \"2022-07-01\"]
   ```
   "
  [year month day-of-week]
  (let [dow (get days-of-week day-of-week)]
    (->> (LocalDate/of year month 1)
         (iterate #(.plusDays % 1))
         (drop-while #(not= dow (.getDayOfWeek %)))
         first)))

(defn same-month [month ^java.time.LocalDate date]
  (= month (.getMonthValue date)))

(defn find-all-dow-in-month 
  "Given a date, returns a vector of all days of month (1..31), in the same month 
   and with the same day of week.
   
   Example: returns day of month for all friday in July 2022
   ```
   (find-all-dow-in-month (find-first-dow 2022 07 :friday))
   => [1 8 15 22 29]
   ```   
   "
  [^LocalDate first-dow]
  (let [in-same-month? (partial same-month (.getMonthValue first-dow))]
    (->> first-dow
         (iterate #(.plusWeeks % 1))
         (take-while in-same-month?)
         (mapv #(.getDayOfMonth %)))))

(defn teenth? [n] (> 20 n 12))
(def not-teenth? (complement teenth?))

(def selectors {:first   first
                :second  second
                :third   #(get % 2)
                :fourth  #(get % 3)
                :fifth   #(get % 4)
                :last    last
                :teenth  #(first (drop-while not-teenth? %))})

(defn meetup [month year day-of-week descriptor]
  (let [first-day-of-wweek (find-first-dow year month day-of-week)
        candidates         (find-all-dow-in-month first-day-of-wweek)
        select-fn          (get selectors descriptor)]
    [year month (select-fn candidates)]))