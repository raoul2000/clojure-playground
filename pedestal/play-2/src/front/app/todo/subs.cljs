(ns app.todo.subs
  (:require [re-frame.core :as rf]
            [shared.db :as db]))

;; ------ Layer 2 - Extractors

(defn query-todo-list [db v] (:todos db))
(rf/reg-sub :todo-list  query-todo-list)

(rf/reg-sub  :todo-list-title
             (fn [db _]
               (get-in db [:todos :todo-list/title])))

(rf/reg-sub  :todo-items-id
             (fn [db _]
               (db/read-todo-ids (:todos db))))

(rf/reg-sub  :todo-list-items
             (fn [db _]
               (get-in db [:todos :todo-list/items])))

(rf/reg-sub  :todo-edit-id
             (fn [db _]
               (:todo-edit-id db)))

(rf/reg-sub :todo-item
            (fn [db [_ id]]
              (db/read-todo-by-id (:todos db) id)))

(rf/reg-sub :loading?
            (fn [db [_ id]]
              (:loading? db)))

;; ------ Layer 3 - Materialised View

(rf/reg-sub
 :todo-items-count
 (fn [query-v]
   (rf/subscribe [:todo-list-items]))

 (fn [todo-list-items v]
   (count todo-list-items)))


