(ns triangle)

(defn is-valid? [a b c]
  (let [v [a b c]
        m (apply max v)]
    (and
     (not-every? zero? [a b c])
     (< (- m (apply + v)) 0))))

(comment
  (is-valid? 1 3 1)
  (is-valid? 0 0 0)
  ;;
  )

(defn equilateral? [a b c]
  (and
   (is-valid? a b c)
   (= a b c)))

(defn isosceles? [a b c]
  (when (is-valid? a b c)
    (or
     (equilateral? a b c)
     (= a b)
     (= a c)
     (= b c))))

(comment
  (isosceles? 7 3 2)
  ;;
  )

(defn scalene? [a b c]
  (not (isosceles? a b c)))

(comment
  (scalene? 7 3 2)
  ;;
  )
