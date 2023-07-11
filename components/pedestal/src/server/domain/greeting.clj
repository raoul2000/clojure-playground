(ns server.domain.greeting
  (:require [clojure.string :as s]))


(defn greeting-for [name]
  (tap> name)
  (cond
    (nil? name)            "hello, stranger ...."
    (#{"bob" "max"} name)  nil
    :else                  (str "hello, " (if (s/blank? name)
                                            "mysterious"
                                            name))))