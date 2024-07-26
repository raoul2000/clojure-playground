(ns coding-game.thor)


(defn translate2d
  "Returns a function making use of a closure to
   perform a repeatable 2d translation of a coordinate pair."
  [dx dy]
  (fn [x y]
    [(+ x dx) (+ y dy)]))

(defn scale2d
  "Returns a function making use of a closure to
   perform a repeatable 2d scale of a coordinate pair."
  [sx sy]
  (fn [x y]
    [(* x sx) (* y sy)]))


(defn compose-transform
  "Create a composition function that returns a function that 
   combines two functions to perform a repeatable transformation."
  [f g]
  (fn [x y]
    (apply g (f x y))))

(defn memoize-transform
  "Returns a function that memoizes the last result.
   If the arguments are the same as the last call,
   the memoized result is returned."
  [f]
  (let [arg (atom {})]
    (fn [a b]
      (if-let [res (get @arg [a b])]
        res
        (let [res (f a b)]
          (swap! arg assoc [a b] res)
          res)))))



(comment

  (def f (memoize-transform (translate2d -6 10)))

  (f 1 )

  ((compose-transform (translate2d -6 10)
                      (scale2d 3 2))   0 0)





  ;;
  )