(ns todo.subs
  (:require [re-frame.core :as rf]))

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

