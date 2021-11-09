(ns triangle)

(defn valid-triangle? [a b c]
  (let [sizes (sort [a b c])]
    (and
     (not-every? zero? sizes)
     (> (+ (first sizes) (second sizes)) (last sizes)))))

(defn equilateral? [a b c]
  (and
   (valid-triangle? a b c)
   (= a b c)))

(defn isosceles? [a b c]
  (and
   (valid-triangle? a b c)
   (or
     (equilateral? a b c)
     (= 2 (count (into #{} [a b c]))))))

(defn scalene? [a b c]
  (and
   (valid-triangle? a b c)
   (not (isosceles? a b c))))

