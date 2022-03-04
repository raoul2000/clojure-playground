(ns todo.db)

;; todo model is a map 
;; - key = id : a String identifier
;; - key = text : a String describing the task todo
;;
;; example :
;; {:id "112365"
;;  :text "by some milk"
;;  :done true}


(def default-db      ;; what gets put into app-db by default.
  {:todos   []       ;; an empty list of todos. Use the (int) :id as the key
   })