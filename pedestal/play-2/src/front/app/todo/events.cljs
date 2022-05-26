(ns app.todo.events
  (:require [re-frame.core :as rf]
            [app.todo.db :as db]))

(defn dispatch-initialize-todo []
  (rf/dispatch-sync [:initialize-todo]))

(rf/reg-event-db
 :initialize-todo
 (fn [_ _]
   {:todos db/initial-todo-list
    :todo-edit-id nil}))

(rf/reg-event-db
 :toggle-todo-item
 (fn [db [_ id]]
   (let [todo-list (:todos db)
         todo      (db/read-todo-by-id todo-list id)
         new-list  (db/update-todo todo-list
                                   id
                                   (update todo :todo/done not))]
     (assoc db :todos new-list))))

(rf/reg-event-db
 :delete-todo-item
 (fn [db [_ id]]
   (update db :todos #(db/delete-todo % id))))

(rf/reg-event-db
 :edit-todo-item
 (fn [db [_ id]]
   (assoc db :todo-edit-id id)))

(rf/reg-event-db
 :cancel-edit-todo
 (fn [db _]
   (assoc db :todo-edit-id nil)))

(rf/reg-event-db
 :update-todo-title
 (fn [db [_ id title]]
   (-> db
       (update :todos #(db/update-todo-title % id title))
       (assoc :todo-edit-id nil))))

(rf/reg-event-db
 :add-todo-item
 (fn [db _]
   (let [new-todo (db/create-todo "Enter your description ..." false)]
     (-> db
         (update :todos #(db/add-todo-to-list % new-todo))
         (assoc :todo-edit-id (:todo/id new-todo))))))

