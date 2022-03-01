(ns todo.db)

(def default-db      ;; what gets put into app-db by default.
  {:todos   []       ;; an empty list of todos. Use the (int) :id as the key
   :form    ""       ;; text in the todo form
   })