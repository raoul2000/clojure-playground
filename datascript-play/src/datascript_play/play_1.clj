(ns datascript-play.play-1
  (:require [datascript.core :as d]))



(let [schema {:aka {:db/cardinality :db.cardinality/many}}
      conn   (d/create-conn schema)]

  (d/transact! conn [{:db/id -1
                      :name  "Maksim"
                      :age   45
                      :aka   ["Max Otto von Stierlitz", "Jack Ryan"]}])

  (d/transact! conn [{:db/id -1
                      :name  "Marley"
                      :age   25
                      :aka   ["Bob", "Bobby" "Jack Ryan"]}])

  ;; find name and age of all persons aka "Jack Ryan"
  (d/q '[:find  ?n ?a
         :where [?e :aka "Jack Ryan"]
         [?e :name ?n]
         [?e :age  ?a]]
       @conn))