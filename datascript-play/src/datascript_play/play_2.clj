(ns datascript-play.play-2
  (:require [datascript.core :as d]))

(comment

  (def schema {:name {:db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc "A person's name"}})
  ;; fails
  ;; Bad attribute specification for {:name #:db{:valueType :db.type/string}}, expected one of #{:db.type/tuple :db.type/ref}
  (def conn   (d/create-conn schema))

  ;; when https://github.com/kristianmandrup/datascript-tutorial/blob/master/create_schema.md#datascript-schemas
  ;; ".. You can still add the value type if you like!"
  ;;
  ;; following example fails:
  ;;(def schema {:person/name {:db/valueType :db.type/string
  ;;                           :db/cardinality :db.cardinality/one
  ;;                           :db/doc "A person's name"}})
  ;;
  )

(comment

  (def schema {})
  (def conn (d/create-conn {}))

  (d/transact! conn [{:db/id -1
                      :name  "Maksim"
                      :age   45
                      :aka   ["Max Otto von Stierlitz", "Jack Ryan"]}])

  (d/transact! conn [{:db/id -1
                      :name  "Maksim"
                      :age   "eee"
                      :aka   ["Max Otto von Stierlitz", "Jack Ryan"]}])

  (d/q '[:find ?name
         :where 
         [?e :age 45]
         [?e :name ?name]]
       @conn)

  ;;
  )


