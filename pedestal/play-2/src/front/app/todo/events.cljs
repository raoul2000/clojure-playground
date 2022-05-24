(ns app.todo.events
  (:require [re-frame.core :as rf]
            [app.todo.db :as db]))

(defn dispatch-initialize-todo []
  (rf/dispatch-sync [:initialize-todo]))


(rf/reg-event-db
 :initialize-todo
 (fn [_ _]
   {:todos db/initial-todo-list}))

(rf/reg-event-db
 :toggle-todo-item
 (fn [db [id]]
   (let [todo-list (:todos db)
         todo (db/read-todo-by-id todo-list id)]
     {:todos (db/update-todo todo-list
                             id
                             (assoc todo :todo/done true))})))

