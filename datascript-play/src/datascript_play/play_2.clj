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
  ;; see https://github.com/tonsky/datascript/issues/407#issuecomment-951883255
  )

(comment

  (def schema {})
  (def conn (d/create-conn {}))

  (d/transact! conn [{:db/id 1
                      :person/name  "Maksim"
                      :person/age   45
                      :person/aka   ["Max Otto von Stierlitz", "Jack Ryan"]}])

  (d/transact! conn [{:db/id 2
                      :person/name  "Junior"
                      :person/age   12
                      :person/aka   ["Jack Ryan"]}])

  (d/transact! conn [{:db/id 3
                      :person/name  "Alice"
                      :person/age   45
                      :person/aka   ["Al ice"]}])

  ;; find all person names
  (d/q '[:find ?name
         :where
         [?e :person/name ?name]]
       @conn)

  ;; find all name and entity ids of persons older than 15
  (d/q '[:find ?name ?e
         :where
         [?e :person/age ?age]
         [(> ?age 20)]
         [?e :person/name ?name]]
       @conn)

  ;;
  )

(comment
  ;; model file system 
  ;; folder A
  ;;    +------- file1.txt
  ;;    +------- file2.txt
  ;; folder B
  ;;    +------- file1.txt
  ;;             folder 3
  ;;                 +------ file 3.txt

  (def schema {:path/name {:db/unique :db.unique/identity
                           :db/doc "a file system path normalized"}})
  (def conn (d/create-conn  {}))

  (d/transact! conn [{:db/id -1
                      :folder/name "A"
                      :folder/parent 0}
                     {:file/name "file1.txt"
                      :folder/parent  -1}
                     {:file/name "file2.txt"
                      :folder/parent  -1}
                     {:db/id -2
                      :folder/name "B"
                      :folder/parent 0}
                     {:file/name "file2.txt"
                      :folder/parent  -2}
                     {:db/id -3
                      :folder/name "C"
                      :folder/parent -2}
                     {:file/name "file3.txt"
                      :folder/parent  -3}])
  
  (d/q '[:find ?parent-folder :where
         [?parent-folder :folder/name "A"]
         ;;[?file :folder/parent ?parent-folder]
         ;;[?file :file/name ?filename]
         
         ] @conn)
  ;;
  )



