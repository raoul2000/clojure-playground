(ns app.todo.subs
  (:require [re-frame.core :as rf]))

;; ------ Layer 2 - Extractors

(defn query-todo-list [db v] (:todos db))
(rf/reg-sub :todo-list  query-todo-list)

(rf/reg-sub  :todo-list-title
             (fn [db _]
               (get-in db [:todos :todo-list/title])))

(rf/reg-sub  :todo-items-id
             (fn [db _]
               (mapv #(:todo/id %) (:todo-list/items db))))

(rf/reg-sub  :todo-list-items
             (fn [db _]
               (get-in db [:todos :todo-list/items])))

(rf/reg-sub  :todo-edit-id
             (fn [db _]
               (:todo-edit-id db)))

;; ------ Layer 3 - Materialised View

(rf/reg-sub
 :todo-items-count
 (fn [query-v]
   (rf/subscribe [:todo-list-items]))

 (fn [todo-list-items v]
   (count todo-list-items)))


