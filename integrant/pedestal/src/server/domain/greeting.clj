(ns server.domain.greeting
  (:require [clojure.string :as s]))


(defn greeting-for [name {:keys [polite?] :as opts}]
  (tap> (str "name = " name))
  (tap> opts)
  (cond
    (nil? name)            "hello, stranger ..."
    (#{"bob" "max"} name)  (when polite? ", sorry I'm busy")
    :else                  (str "hello, " (if (s/blank? name)
                                            "mysterious"
                                            name))))