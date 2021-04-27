(ns clock)

(defn clock->string [clock] ;; <- arglist goes here
  (format "%02d:%02d"
          (first clock)
          (second clock)))

(defn clock [hours minutes] ;; <- arglist goes here
  (let [m (mod minutes 60)
        min-offset (quot minutes 60)
        h (mod (+ hours  min-offset) 24)]
    [h m]))

(defn add-time [clock time] ;; <- arglist goes here
  ;; your code goes here
  )

(comment
  (= [1 2] '(1 2) #{1 2})
  (clock->string (clock 12 00))
  (clock 24 00)
  (clock 25 00)
  (clock 100 00)
  (clock 1 60)
  (clock 0 160)
  (clock 0 1723)
  (clock 25 160)
  (clock 201 3001)
  (clock 72 8640)
  (clock -1 15)

  (clock -25 -160)
  ; 20:20
  (clock -121 -5810))
