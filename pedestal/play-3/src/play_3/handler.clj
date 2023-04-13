(ns play-3.handler)

(defn greeting-for [name]
  (cond
    (nil? name)            "hello, stranger"
    (#{"bob" "max"} name)  nil
    :else                  (str "hello, " name)))