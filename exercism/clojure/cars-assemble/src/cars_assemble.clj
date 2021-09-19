(ns cars-assemble)

(def cars-per-hour 221)

(defn success-rate-0 [speed]
  (cond
    (zero? speed) 0.0
    (< 0 speed 5) 1.0
    (< 4 speed 9) 0.9
    (= 9 speed)   0.8
    :else         0.77))

(defn success-rate-2 [speed]
  (case speed
    0         0.0
    (1 2 3 4) 1.0
    (5 6 7 8) 0.9
    9         0.8
    0.77))



(defn production-rate-0
  "Returns the assembly line's production rate per hour,
   taking into account its success rate"
  [speed]
  (* (* speed cars-per-hour) (success-rate-2 speed)))

(defn production-rate
  "Returns the assembly line's production rate per hour,
   taking into account its success rate"
  [speed]
  (let [success-rate (case speed
                       0         0.0
                       (1 2 3 4) 1.0
                       (5 6 7 8) 0.9
                       9         0.8
                       0.77)]
    (* (* speed cars-per-hour) success-rate)))

(defn production-rate-2
  "Returns the assembly line's production rate per hour,
   taking into account its success rate"
  [speed]
  (->> speed
       (* cars-per-hour)
       (* (success-rate-2 speed))))

(defn working-items
  "Calculates how many working cars are produced per minute"
  [speed]
  (int (/ (production-rate speed) 60)))
