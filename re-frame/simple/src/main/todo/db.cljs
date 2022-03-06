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
   :loading false    ;; when TRUE, the todo list is being loaded .. please wait
   :show   :all      ;; filter to display todos
   })


(defn select-filter [filter-id]
  (case filter-id
    "done"   :done
    "undone" (comp not :done)
    nil))

(defn filter-todo [filter-id todos]
  (if-let [filter-fn (select-filter filter-id)]
    (filter filter-fn todos)
    todos))