(ns server.domain.greeting
  (:require [clojure.string :as s]))


(defn greeting-for [name polite?]
  (cond
    (nil? name)            "hello, stranger ..."
    (#{"bob" "max"} name)  (when polite?  (str  ", sorry " name ", I'm busy"))
    :else                  (str "hello, " (if (s/blank? name)
                                            "mysterious"
                                            name))))