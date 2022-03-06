(ns todo.subs
  (:require [re-frame.core :as rf]
            [todo.db :refer [filter-todo]]))

;; Layer 2 ----------------------------------------------------------
(rf/reg-sub
 :todos
 (fn [db _]
   (:todos db)))

(rf/reg-sub
 :todo-ids
 (fn [db _]
   (map :id (:todos db))))

(rf/reg-sub
 :todo-info
 (fn [db [_ todo-id]]
   (first (filter #(= todo-id (:id %)) (:todos db)))))

(rf/reg-sub
 :loading
 (fn [db _]
   (:loading db)))

(rf/reg-sub
 :filter
 (fn [db _]
   (:show db)))

;; Layer 3  ------------------------------------------------------

(rf/reg-sub
 :todos-count

 (fn [_]
   [(rf/subscribe [:todos])])

 (fn [[todos] _]
   (count todos)))


(rf/reg-sub
 :todos-done-count

 (fn [_]
   [(rf/subscribe [:todos])])
 (fn [[todos] _]
   (count (filter :done todos))))

(rf/reg-sub
 :filtered-todo-ids

 (fn [_]
   [(rf/subscribe [:todos])
    (rf/subscribe [:filter])])

 (fn [[todos filter-id] _]
   (js/console.log filter-id)
   (map :id (filter-todo filter-id todos))))